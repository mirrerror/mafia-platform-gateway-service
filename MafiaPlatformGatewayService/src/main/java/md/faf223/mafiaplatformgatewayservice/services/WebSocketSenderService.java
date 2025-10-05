package md.faf223.mafiaplatformgatewayservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketSenderService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToClients(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

}