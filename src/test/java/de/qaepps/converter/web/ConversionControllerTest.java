package de.qaepps.converter.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jodconverter.core.office.OfficeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import de.qaepps.converter.service.ConverterService;

@Tag("mockTest")
@ExtendWith(MockitoExtension.class)
class ConversionControllerTest {

	// class under test
	private ConversionController conversionController;

	@Mock 
	private  ConverterService converterService;

	@BeforeEach
	void setUp() {
		conversionController = new ConversionController(converterService);
	}

	@Test
	@DisplayName("convertToPdf should be sucessful")
	void convertToPdfShouldBeSucessful() throws IOException, OfficeException {
		// prepare
		when(converterService.convert(any(), any(), any())).thenReturn(new ByteArrayOutputStream());    
		var multiPartFile = new MockMultipartFile("file", "any-name.xyz", null, new ByteArrayInputStream("any".getBytes()));

		// test
		ResponseEntity<Object> response = conversionController.convertToPdf(multiPartFile);

		// validate
		verify(converterService).convert(any(), any(), any());
		
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertNotNull(response.getHeaders());
		
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody(), isA(byte[].class));
	}
	
	@Test
	@DisplayName("convertToPdf should fail")
	void convertToPdfShouldFail() throws IOException, OfficeException {
		// prepare
		when(converterService.convert(any(), any(), any())).thenThrow(new OfficeException("any"));    
		var multiPartFile = new MockMultipartFile("file", "any-name.xyz", null, new ByteArrayInputStream("any".getBytes()));

		// test
		ResponseEntity<Object> response = conversionController.convertToPdf(multiPartFile);

		// validate
		verify(converterService).convert(any(), any(), any());
		
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertNotNull(response.getHeaders());
		
		assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
		assertThat(response.getBody(), isA(String.class));
	}
}