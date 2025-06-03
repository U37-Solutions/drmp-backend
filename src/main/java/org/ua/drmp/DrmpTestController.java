package org.ua.drmp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zalupa")
public class DrmpTestController {

	@GetMapping
	public void sendZalupaToU37(){
	}

	@GetMapping("/blabla")
	public String userAccess() {
		return "Hello User or Admin!";
	}

	@GetMapping("/admin/blabla")
	public String adminAccess() {
		return "Hello Admin!";
	}
}
