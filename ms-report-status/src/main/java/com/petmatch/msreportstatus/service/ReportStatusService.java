package com.petmatch.msreportstatus.service;
import com.petmatch.msreportstatus.dto.ReportStatusDTO;
import com.petmatch.msreportstatus.dto.ReportStatusResponseDTO;
import com.petmatch.msreportstatus.exception.ResourceNotFoundException;
import com.petmatch.msreportstatus.mapper.ReportStatusMapper;
import com.petmatch.msreportstatus.model.ReportStatus;
import com.petmatch.msreportstatus.repository.ReportStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class ReportStatusService {
    private final ReportStatusRepository repo;
    private final ReportStatusMapper mapper;
    public List<ReportStatusResponseDTO> getAll(){ return repo.findAll().stream().map(mapper::toDTO).toList(); }
    public ReportStatusResponseDTO getById(Long id){
        return mapper.toDTO(repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Estado con id "+id+" no encontrado")));
    }
    public ReportStatusResponseDTO save(ReportStatusDTO dto){
        repo.findByNameIgnoreCase(dto.getName()).ifPresent(e->{throw new IllegalArgumentException("Ya existe el estado: "+dto.getName());});
        return mapper.toDTO(repo.save(mapper.toEntity(dto)));
    }
    public ReportStatusResponseDTO update(Long id,ReportStatusDTO dto){
        ReportStatus e=repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Estado con id "+id+" no encontrado"));
        e.setName(dto.getName()); return mapper.toDTO(repo.save(e));
    }
    public void delete(Long id){
        repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Estado con id "+id+" no encontrado"));
        repo.deleteById(id);
    }
}
