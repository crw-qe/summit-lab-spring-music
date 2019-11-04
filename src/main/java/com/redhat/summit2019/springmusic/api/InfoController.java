package com.redhat.summit2019.springmusic.api;

import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.summit2019.springmusic.domain.ApplicationInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "appInfo", description = "Application Information")
public class InfoController {
	private final Environment springEnvironment;

	public InfoController(Environment springEnvironment) {
		this.springEnvironment = springEnvironment;
	}

	@Operation(description = "Get application information", tags = { "appInfo" })
	@ApiResponse(responseCode = "200", description = "Success!")
	@GetMapping(path = "/appinfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ApplicationInfo info() {
		return new ApplicationInfo(this.springEnvironment.getActiveProfiles(), new String[0]);
	}
}
