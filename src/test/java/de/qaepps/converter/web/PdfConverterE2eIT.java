package de.qaepps.converter.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

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

@Tag("e2eTest")
class PdfConverterE2eIT {

  private static final Logger LOG = LoggerFactory.getLogger(PdfConverterE2eIT.class);
  
  private static final String REQUEST_URL = "http://localhost:8100/convert/toPdf";

//  @BeforeEach
//  public void init() {
//      dokumentITUtil.fixHeadless();
//      requestUrl = configService.getOpenOfficeConverterUrl() + "/convert/toPdf";
//  }
  
  @ParameterizedTest
  @ValueSource(strings = { "any.docx", "any.dotx", "any.xlsx", "any.xltx"})
  @DisplayName("should convert office docs to pdf")
  void convertOfficeDocsToPdf(String filename) {
	  // prepare	  
	  HttpEntity<MultiValueMap<String, Object>> requestEntity = createRequestEntity(filename);
	  
	  // test
	  ResponseEntity<byte[]> response = new RestTemplate().postForEntity(REQUEST_URL, requestEntity, byte[].class);
	  
	  // validate
	  assertNotNull(response);
	  assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  @DisplayName("should convert docx to pdf 500 times")
  void stressConverter()  {
	  // prepare
	  HttpEntity<MultiValueMap<String, Object>> requestEntity = createRequestEntity("any.docx");
	  
	  // test
	  int limit = 500;
	  RestTemplate restTemplate = new RestTemplate();
	  ResponseEntity<byte[]> response;
	  LOG.info("stress pdf-converter with {} conversions", limit);
	  for (int i = 0; i < limit; i++) {
		  long started = System.currentTimeMillis();
		  response = restTemplate.postForEntity(REQUEST_URL, requestEntity, byte[].class);
		  LOG.info("conversion no {} took {} millis", i, System.currentTimeMillis() - started);
		  
		  assertNotNull(response);
		  assertThat(response.getStatusCode(), is(HttpStatus.OK));
	  } 
  }
  
	private HttpEntity<MultiValueMap<String, Object>> createRequestEntity(String fileName) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new ClassPathResource("/officefiles/" + fileName));

		return new HttpEntity<>(body, createHeaders());
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		return headers;
	}

  
}