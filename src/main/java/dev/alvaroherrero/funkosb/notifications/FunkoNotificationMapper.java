package dev.alvaroherrero.funkosb.notifications;

import dev.alvaroherrero.funkosb.funko.model.Funko;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {

    public FunkoNotificationDTO toDTO(Funko funko) {
        return FunkoNotificationDTO
                .builder()
                .nombre(funko.getName())
                .precio(funko.getPrice())
                .categoria(funko.getCategory().getCategory())
                .build();
    }
}
