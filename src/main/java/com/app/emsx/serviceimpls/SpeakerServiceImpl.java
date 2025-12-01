package com.app.emsx.serviceimpls;
import com.app.emsx.dtos.SpeakerDTO;
import com.app.emsx.entities.Speaker;
import com.app.emsx.mappers.SpeakerMapper;
import com.app.emsx.repositories.SpeakerRepository;
import com.app.emsx.services.SpeakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpeakerServiceImpl implements SpeakerService {

    @Autowired
    private SpeakerRepository speakerRepository;

    private String getCurrentUserEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    @Override
    public SpeakerDTO createSpeaker(SpeakerDTO speakerDTO) {
        Speaker speaker = SpeakerMapper.mapSpeakerDTOToSpeaker(speakerDTO);
        speaker.setOwnerEmail(getCurrentUserEmail());
        Speaker saved = speakerRepository.save(speaker);
        return SpeakerMapper.mapSpeakerToSpeakerDTO(saved);
    }

    @Override
    public SpeakerDTO updateSpeaker(Long id, SpeakerDTO speakerDTO) {
        String ownerEmail = getCurrentUserEmail();
        Speaker speaker = speakerRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new RuntimeException("Speaker no encontrado o no pertenece al usuario actual con id: " + id));
        speaker.setFullName(speakerDTO.getNombre());
        speaker.setBio(speakerDTO.getBio());
        speaker.setEmail(speakerDTO.getEmail());
        speaker.setCompany(speakerDTO.getCompany());
        speaker.setDateNac(speakerDTO.getDate_nac());

        Speaker updated = speakerRepository.save(speaker);
        return SpeakerMapper.mapSpeakerToSpeakerDTO(updated);
    }

    @Override
    public void deleteSpeaker(Long id) {
        String ownerEmail = getCurrentUserEmail();
        if(!speakerRepository.existsByIdAndOwnerEmail(id, ownerEmail)) {
            throw new RuntimeException("Speaker no encontrado o no pertenece al usuario actual con id: " + id);
        }
        speakerRepository.deleteById(id);
    }

    @Override
    public List<SpeakerDTO> findAllSpeakers() {
        String ownerEmail = getCurrentUserEmail();
        List<Speaker> speakers = speakerRepository.findAllByOwnerEmail(ownerEmail);
        return speakers.stream()
                .map(SpeakerMapper::mapSpeakerToSpeakerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SpeakerDTO findSpeakerById(Long id) {
        String ownerEmail = getCurrentUserEmail();
        Speaker speaker = speakerRepository.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new RuntimeException("Speaker no encontrado o no pertenece al usuario actual con id: " + id));
        return SpeakerMapper.mapSpeakerToSpeakerDTO(speaker);
    }
}
