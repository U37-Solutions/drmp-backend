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
import org.ua.drmp.company.dto.CFieldValueDto;
import org.ua.drmp.company.service.CFieldValueService;

@RestController
@RequestMapping("/custom-values")
@RequiredArgsConstructor
public class CFieldValueController {

	private final CFieldValueService cFieldValueService;

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<List<CFieldValueDto>> getAll() {
		return ResponseEntity.ok(cFieldValueService.fetchAll());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<CFieldValueDto> getById(@PathVariable Long id) {
		return ResponseEntity.ok(cFieldValueService.fetchById(id));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<CFieldValueDto> create(@RequestBody CFieldValueDto dto) {
		return ResponseEntity.ok(cFieldValueService.create(dto));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<CFieldValueDto> update(@PathVariable Long id, @RequestBody CFieldValueDto dto) {
		return ResponseEntity.ok(cFieldValueService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		cFieldValueService.delete(id);
		return ResponseEntity.noContent().build();
	}
}