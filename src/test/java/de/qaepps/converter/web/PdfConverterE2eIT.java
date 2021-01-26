package de.qaepps.converter.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * end-to-end tests with dockerized pdf-converter
 * <p>
 * Testcontainers creates and runs a image-on-the-fly with pdf-converter included<br>
 * After the tests the image will be removed
 * 
 * @precondition packed pdf-converter-$version.jar (gradle build) and running docker-daemon
 * 
 * @author haege
 *
 */
@Tag("e2eTest")
@Testcontainers
class PdfConverterE2eIT {

	private static final Logger LOG = LoggerFactory.getLogger(PdfConverterE2eIT.class);

	private static final String API = "/convert/toPdf";
	private static final int EXPOSED_PORT = 8100;
    private static final String TARGET_DIR = "build/converted/byDockerImage";

	private static String getUrl() {
		return "http://" + appContainer.getHost() + ":" + appContainer.getMappedPort(EXPOSED_PORT) + API;
	}

	@Container
	public static GenericContainer<?> appContainer = new GenericContainer<>(
			new ImageFromDockerfile()
			.withDockerfile((new File("./Dockerfile")).toPath())
			).waitingFor(
					Wait.forLogMessage(".*Started PdfConverterApplication.*", 1)
					);

	@ParameterizedTest
	@ValueSource(strings = { "any.docx", "any.dotx", "any.xlsx", "any.xltx"})
	@DisplayName("should convert office docs to pdf")
	void convertOfficeDocsToPdf(String filename) throws Exception {
		// prepare	  
		HttpEntity<MultiValueMap<String, Object>> requestEntity = createRequestEntity("/officefiles/" + filename);

		// test
		ResponseEntity<byte[]> response = new RestTemplate().postForEntity(getUrl(), requestEntity, byte[].class);

		// validate
		assertNotNull(response);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));

		saveContentToFile(filename, response.getBody());
	}

    @ParameterizedTest
    @ValueSource(strings = { "any.json", "any.txt", "any.xml", "any.xyz"})
    @DisplayName("should convert textfiles to pdf")
    void convertTextDocsToPdf(String filename) throws Exception {
		// prepare	  
		HttpEntity<MultiValueMap<String, Object>> requestEntity = createRequestEntity("/textfiles/" + filename);

		// test
		ResponseEntity<byte[]> response = new RestTemplate().postForEntity(getUrl(), requestEntity, byte[].class);

		// validate
		assertNotNull(response);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		
		saveContentToFile(filename, response.getBody());
	}

	@Test
	@DisplayName("should convert docx to pdf 500 times")
	void stressConverter()  {
		// prepare
		HttpEntity<MultiValueMap<String, Object>> requestEntity = createRequestEntity("/officefiles/" + "any.docx");

		// test
		int limit = 500;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<byte[]> response;
		LOG.info("stress pdf-converter with {} conversions", limit);
		for (int i = 0; i < limit; i++) {
			long started = System.currentTimeMillis();
			response = restTemplate.postForEntity(getUrl(), requestEntity, byte[].class);
			LOG.info("conversion no {} took {} millis", i, System.currentTimeMillis() - started);

			assertNotNull(response);
			assertThat(response.getStatusCode(), is(HttpStatus.OK));
		} 
	}

	private HttpEntity<MultiValueMap<String, Object>> createRequestEntity(String fileName) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new ClassPathResource(fileName));

		return new HttpEntity<>(body, createHeaders());
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		return headers;
	}

	private void saveContentToFile(String filename, byte[] content) throws IOException {
		FileUtils.writeByteArrayToFile(new File(TARGET_DIR, filename + ".pdf"), content);
	}

}