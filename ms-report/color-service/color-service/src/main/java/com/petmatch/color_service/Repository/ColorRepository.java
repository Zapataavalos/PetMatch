package com.petmatch.color_service.Repository;

import com.petmatch.color_service.Model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {

    boolean existsByNombreColorIgnoreCase(String nombreColor);

    boolean existsByCodigoHexadecimalIgnoreCase(String codigoHexadecimal);

    boolean existsByNombreColorIgnoreCaseAndIdColorNot(String nombreColor, Integer idColor);

    boolean existsByCodigoHexadecimalIgnoreCaseAndIdColorNot(String codigoHexadecimal, Integer idColor);
}