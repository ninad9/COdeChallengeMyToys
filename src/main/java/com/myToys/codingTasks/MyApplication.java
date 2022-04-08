package com.myToys.codingTasks;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class MyApplication {
	
	
	/** 						TASK #1
	 * Returns Set of ProductDto as the output 
	 * Set is used to remove any duplicate entries if made in the input sheet  
	 * No arguments is required for input
	 * */
	@SuppressWarnings("finally")
	@GetMapping("/product")
	public Set<ProductDto> products() {
		//Fetch input CSV from resource folder//
		String filePath = "src/main/resources/product_data.csv";
		String line = "";
		ProductDto dto = null;
		Set<ProductDto> set = new HashSet<ProductDto>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(",");
				dto = new ProductDto(data[0], data[1], data[2], data[3], data[4], data[5]);
				set.add(dto);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			return set;
		}

	}
	
	
	/**
	 * 								TASK #2
	 * Returns a Map containing patentApplicationNumber as the key and expiryDate:yyyy-mm-dd as the value 
	 * Receives input in the form of String List of patentApplicationNumber
	 * */
	
	@PostMapping("/getPatentExpiry")
	public List<PatentDto> getPatentExpiry(@RequestBody ArrayList<String> patentNumbers) {
		//Map<String, String> result = fetchPatentDetails(patentNumbers);
		List<PatentDto> result = fetchPatentDetails(patentNumbers);
		return result;
	}

	private List<PatentDto> fetchPatentDetails(ArrayList<String> patentNumbers) {
		StringBuilder patentNums = new StringBuilder();
		patentNumbers.forEach(patent -> patentNums.append(patent + ","));
		String s = patentNums.substring(0, patentNums.length() - 1);
		String s1 = "&start=0&rows=100&largeTextSearchFlag=N";
		URL url;
		List<PatentDto> result = new ArrayList<PatentDto>();
		try {
			
			/*<=== By-pass the SSL handshake Starts ===>*/
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
			SSLContext.setDefault(ctx);
			/*<=== By-pass the SSL handshake Ends ===>*/
			
			/*<=== URL connection with USPTO Starts ===>  */
			url = new URL("https://developer.uspto.gov/ibd-api/v1/application/publications?patentApplicationNumber=" + s
					+ s1);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Language", "en-US");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			
			/*<=== Response Handling Starts ===>  */
			InputStream responseBody = null;
			if (isGzipResponse(conn)) {
				responseBody = new GZIPInputStream(conn.getInputStream());  //since the data received is having GZIP encoding
			} else {
				responseBody = conn.getInputStream();
			}
			
				// converting Input stream response to String   //
			String response = convertStreamToString(responseBody);
			
			/*<== Process received response to desired output starts ==>*/
			String a = response.replaceAll("\"", "");

			String b = "patentApplicationNumber:US\\d+";
			String c = "filingDate:\\d{2}-\\d{2}-\\d{4}";
			Pattern pattern = Pattern.compile(b, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(a);

			Pattern pattern1 = Pattern.compile(c, Pattern.CASE_INSENSITIVE);
			Matcher matcher1 = pattern1.matcher(a);
			List<String> key = new ArrayList<String>();
			List<String> value = new ArrayList<String>();
			while (matcher.find()) {
				key.add(matcher.group().split(":")[1]);
			}
			while (matcher1.find()) {
				String[] temp = matcher1.group().split(":")[1].split("-");
				String date = String.valueOf(Integer.parseInt(temp[2]) + 20) + "-" + temp[0] + "-" + temp[1];
				value.add(date);
			}
			for (int i = 0; i < key.size(); i++) {
				PatentDto dto = new PatentDto();
				dto.setPatentApplicationNumber(key.get(i));
				dto.setExpiryDate(value.get(i));
				result.add(dto);
			}
			/*<== Process received response to desired output Ends ==>*/
			
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected boolean isGzipResponse(HttpURLConnection con) {
		String encodingHeader = con.getHeaderField("Content-Encoding");
		return (encodingHeader != null && encodingHeader.toLowerCase().indexOf("gzip") != -1);
	}

	public String convertStreamToString(InputStream in) throws Exception {
		String response;
		if (in != null) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}

			response = new String(baos.toByteArray());
			baos.close();

		} else {
			response = null;
		}
		return response;
	}

	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
