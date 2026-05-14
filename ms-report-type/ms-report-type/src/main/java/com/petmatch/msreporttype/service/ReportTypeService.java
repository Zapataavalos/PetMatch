package com.petmatch.msreporttype.service;
import com.petmatch.msreporttype.dto.ReportTypeDTO;
import com.petmatch.msreporttype.dto.ReportTypeResponseDTO;
import com.petmatch.msreporttype.exception.ResourceNotFoundException;
import com.petmatch.msreporttype.mapper.ReportTypeMapper;
import com.petmatch.msreporttype.model.ReportType;
import com.petmatch.msreporttype.repository.ReportTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class ReportTypeService {
    private final ReportTypeRepository repo;
    private final ReportTypeMapper mapper;
    public List<ReportTypeResponseDTO> getAll() { return repo.findAll().stream().map(mapper::toDTO).toList(); }
    public ReportTypeResponseDTO getById(Long id) {
        return mapper.toDTO(repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Tipo de reporte con id "+id+" no encontrado")));
    }
    public ReportTypeResponseDTO save(ReportTypeDTO dto) {
        repo.findByNameIgnoreCase(dto.getName()).ifPresent(e->{ throw new IllegalArgumentException("Ya existe el tipo: "+dto.getName()); });
        return mapper.toDTO(repo.save(mapper.toEntity(dto)));
    }
    public ReportTypeResponseDTO update(Long id, ReportTypeDTO dto) {
        ReportType e=repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Tipo de reporte con id "+id+" no encontrado"));
        e.setName(dto.getName()); return mapper.toDTO(repo.save(e));
    }
    public void delete(Long id) {
        repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Tipo de reporte con id "+id+" no encontrado"));
        repo.deleteById(id);
    }
}
