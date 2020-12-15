package de.qaepps.converter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Tag("integrationTest")
@SpringBootTest
class ConversionControllerIT { 

	private static final String REQUEST_URL = "/convert/toPdf";

    // controller under test
	@Autowired
	private ConversionController conversionController;

    private MockMvc mockMvc;
    private String targetMimeType;
    
    @BeforeEach
    public void before() {
    	// create mockMvc
    	mockMvc = MockMvcBuilders
    	        .standaloneSetup(conversionController)
    	        .build();
    	
    	targetMimeType = DefaultDocumentFormatRegistry.getFormatByExtension("pdf").getMediaType();
    }

    @ParameterizedTest
    @ValueSource(strings = { "any.docx", "any.dotx", "any.xlsx", "any.xltx"})
    @DisplayName("should convert office docs to pdf")
    void convertOfficeDocsToPdf(String fileName) throws Exception {
		// prepare
    	Resource testDocx = new ClassPathResource("/officefiles/" + fileName);
		var testFile = new MockMultipartFile("file", testDocx.getFilename(), null, testDocx.getInputStream());

		// test & validate
		mockMvc.perform(multipart(REQUEST_URL).file(testFile))
		.andExpect(status().isOk())
		.andExpect(content().contentType(targetMimeType))
		;
	}
    
    @ParameterizedTest
    @ValueSource(strings = { "any.json", "any.txt", "any.xml", "any.xyz"})
    @DisplayName("should convert textfiles to pdf")
    void convertTextDocsToPdf(String fileName) throws Exception {
		// prepare
    	Resource testDocx = new ClassPathResource("/textfiles/" + fileName);
		var testFile = new MockMultipartFile("file", testDocx.getFilename(), null, testDocx.getInputStream());

		// test & validate
		mockMvc.perform(multipart(REQUEST_URL).file(testFile))
		.andExpect(status().isOk())
		.andExpect(content().contentType(targetMimeType))
		;
	}
    
    @ParameterizedTest
    @ValueSource(strings = { "any.jar"})
    @DisplayName("should not convert unknown formats to pdf")
    void convertUnknownFormatsToPdf(String fileName) throws Exception {
		// prepare
    	Resource testDocx = new ClassPathResource("/unknown/" + fileName);
		var testFile = new MockMultipartFile("file", testDocx.getFilename(), null, testDocx.getInputStream());

		// test & validate
		mockMvc.perform(multipart(REQUEST_URL).file(testFile))
		.andExpect(status().is5xxServerError())
		;
	}
}
