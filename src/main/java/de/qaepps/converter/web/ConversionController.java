package de.qaepps.converter.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.qaepps.converter.service.ConverterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/convert")
public class ConversionController {
	
    private static final Logger LOG = LoggerFactory.getLogger(ConversionController.class);

	private final ConverterService converterService;
	private final DocumentFormat targetFormat;
	
	// constructor injection
    public ConversionController(ConverterService converterService) {
    	this.converterService = converterService;
    	this.targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension("pdf");
    }

    @ApiOperation(
    		"Converts the incoming document to a pdf and returns it.")
    @ApiResponses(
    		value = {
    				@ApiResponse(code = 200, message = "Document successfully converted to a pdf."),
    				@ApiResponse(code = 500, message = "An unexpected error occurred.")
    		})
    @PostMapping(path = "toPdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> convertToPdf(
    		@ApiParam(value = "The document to convert a pdf.", required = true)
    		@RequestParam("file") MultipartFile multipartFile) {
		
    	LOG.info("convert to pdf: {}", multipartFile.getOriginalFilename());
		try {
			ByteArrayOutputStream baos = converterService.convert(
					targetFormat, 
					multipartFile.getInputStream(),
					multipartFile.getOriginalFilename());
			
			HttpHeaders headers = createHeaders(FilenameUtils.getBaseName(multipartFile.getOriginalFilename()));
			
			return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
		}
		catch (OfficeException | IOException ex) {
			LOG.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private HttpHeaders createHeaders(String fileBaseName) {
		String targetFilename = String.format("%s.%s", fileBaseName, targetFormat.getExtension());
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + targetFilename);
		headers.setContentType(MediaType.parseMediaType(targetFormat.getMediaType()));
		
		return headers;
	}
}
