package com.app.emsx.serviceimpls;

import com.app.emsx.dtos.ParticipantDTO;
import com.app.emsx.entities.Participant;
import com.app.emsx.mappers.ParticipantMapper;
import com.app.emsx.repositories.ParticipantRepository;
import com.app.emsx.services.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    private String getCurrentUserEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    @Override
    public ParticipantDTO createParticipant(ParticipantDTO dto) {
        Participant p = ParticipantMapper.toEntity(dto);
        p.setOwnerEmail(getCurrentUserEmail());
        Participant saved = participantRepository.save(p);
        return ParticipantMapper.toDTO(saved);
    }

    @Override
    public ParticipantDTO updateParticipant(Long id, ParticipantDTO dto) {
        String ownerEmail = getCurrentUserEmail();
        Participant p = participantRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new RuntimeException("Participant no encontrado o no pertenece al usuario actual con id: " + id));

        ParticipantMapper.updateEntity(p, dto);
        Participant updated = participantRepository.save(p);
        return ParticipantMapper.toDTO(updated);
    }

    @Override
    public void deleteParticipant(Long id) {
        String ownerEmail = getCurrentUserEmail();
        if (!participantRepository.existsByIdAndOwnerEmail(id, ownerEmail)) {
            throw new RuntimeException("Participant no encontrado o no pertenece al usuario actual con id: " + id);
        }
        participantRepository.deleteById(id);
    }

    @Override
    public ParticipantDTO findParticipantById(Long id) {
        String ownerEmail = getCurrentUserEmail();
        Participant p = participantRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new RuntimeException("Participant no encontrado o no pertenece al usuario actual con id: " + id));
        return ParticipantMapper.toDTO(p);
    }

    @Override
    public List<ParticipantDTO> findAllParticipants() {
        String ownerEmail = getCurrentUserEmail();
        return participantRepository.findAllByOwnerEmail(ownerEmail)
                .stream()
                .map(ParticipantMapper::toDTO)
                .collect(Collectors.toList());
    }
}
