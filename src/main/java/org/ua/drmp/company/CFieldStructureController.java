package org.ua.drmp.company;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ua.drmp.company.dto.CFieldStructureDto;
import org.ua.drmp.company.service.CFieldStructureService;

@RestController
@RequestMapping("/custom-structures")
@RequiredArgsConstructor
public class CFieldStructureController {

	private final CFieldStructureService service;

	@GetMapping
	public List<CFieldStructureDto> getAll() {
		return service.fetchAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<CFieldStructureDto> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.fetchById(id));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<CFieldStructureDto> create(@RequestBody CFieldStructureDto dto) {
		return ResponseEntity.ok(service.create(dto));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<CFieldStructureDto> update(@PathVariable Long id, @RequestBody CFieldStructureDto dto) {
		return ResponseEntity.ok(service.update(id, dto));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
