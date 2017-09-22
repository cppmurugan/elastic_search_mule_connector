package org.mule.modules.elasticsearch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.modules.elasticsearch.config.ConnectorConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Connector(name = "elastic-search", friendlyName = "ElasticSearch")
public class ElasticSearchConnector {

	@Config
	ConnectorConfig config;

	@Processor
	public String CheckElasticServerAvailable() {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(
					"http://" + config.getLocalhost() + ":" + config.getPort(),
					HttpMethod.GET, null,
					new ParameterizedTypeReference<String>() {
					});
		} catch (Exception ie) {
			if( ie.getClass().getName().equals("org.springframework.web.client.ResourceAccessException")){
				return "Server is unavailable--"+ie.getMessage();
			}
			ie.printStackTrace();

		}
		return "Server is available";
	}

	@Processor
	public String GetElasticServerVersion() {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(
					"http://" + config.getLocalhost() + ":" + config.getPort(),
					HttpMethod.GET, null,
					new ParameterizedTypeReference<String>() {
					});
		} catch (HttpClientErrorException ie) {
			ie.printStackTrace();

		}
		return responseEntity.getBody();

	}

	/**
	 * Custom processor
	 *
	 * @param friend
	 *            Name to be used to generate a greeting message.
	 * @return A greeting message
	 */
	@Processor
	public String GET(String indexname, String indextype, String documentNumber) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();

		acceptableMediaTypes.add(MediaType.APPLICATION_XML);

		headers.setAccept(acceptableMediaTypes);
		String url = "http://" + config.getLocalhost() + ":" + config.getPort()
				+ "/" + indexname + "/" + indextype + "/" + documentNumber;
		System.out.println("url in Get-->" + url);
		ResponseEntity<String> responseEntity = null;
		HttpEntity entity = new HttpEntity(headers);
		responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity,
				new ParameterizedTypeReference<String>() {
				});
		System.out.println("String-->" + responseEntity.getBody());
		return responseEntity.getBody();

	}

	@Processor
	public String DELETE(String indexname, String indextype,
			String documentNumber) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();

		acceptableMediaTypes.add(MediaType.APPLICATION_XML);

		headers.setAccept(acceptableMediaTypes);
		String url = "http://" + config.getLocalhost() + ":" + config.getPort()
				+ "/" + indexname + "/" + indextype + "/" + documentNumber;
		System.out.println("url in Get-->" + url);
		ResponseEntity<Object> responseEntity = null;
		HttpEntity entity = new HttpEntity(headers);
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.DELETE,
					entity, new ParameterizedTypeReference<Object>() {
					});
			System.out.println("String-->" + responseEntity.getBody());
		} catch (HttpClientErrorException ie) {
			return ie.getMessage();
		} catch (Exception ie) {

			ie.printStackTrace();

		}

		return responseEntity.getBody().toString();
	}

	@Processor
	public Map PUT(String indexname, String indextype, String documentNumber,
			@Default("#[payload]") Map<String,String> requestBody) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

		headers.setAccept(acceptableMediaTypes);

		/*Map<String, String> requestObject = new LinkedHashMap<String, String>();
		requestObject.put("title", "title");
		requestObject.put("director", "director");*/

		String url = "http://" + config.getLocalhost() + ":" + config.getPort()
				+ "/" + indexname + "/" + indextype + "/" + documentNumber;

		HttpEntity<Map<String,String>>  entity = new HttpEntity<Map<String,String>> (
				requestBody, headers);

		ResponseEntity<Map<String, String>> responseEntity = null;
		System.out.println("url-->" + url);
		System.out.println("url-->" + requestBody);
		responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity,
				new ParameterizedTypeReference<Map<String, String>>() {
				});

		System.out.println("Response-->" + responseEntity.getBody());

		return responseEntity.getBody();
	}

	public ConnectorConfig getConfig() {
		return config;
	}

	public void setConfig(ConnectorConfig config) {
		this.config = config;
	}

}