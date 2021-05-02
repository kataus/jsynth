package pro.esteps.jsynth.websocket_api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pro.esteps.jsynth.messaging.message.Message;
import pro.esteps.jsynth.websocket_api.input.DrumMachineMessage;
import pro.esteps.jsynth.websocket_api.input.SynthMessage;

import java.util.Map;

/**
 * Message <-> JSON mapper.
 */
public class MessageMapperImpl implements MessageMapper {

    private static final Map<String, Class<? extends Message>> MESSAGE_TYPE_MAPPING = Map.of(
            "synth", SynthMessage.class,
            "drumMachine", DrumMachineMessage.class
    );

    private final ObjectMapper objectMapper;

    public MessageMapperImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Map JSON to a message.
     *
     * @param json
     * @return
     */
    @Override
    public Message mapJsonToMessage(String json) {
        try {
            var genericMessage = getGenericMessage(json);
            var concreteMessage = getConcreteMessage(json, genericMessage);
            if (concreteMessage == null) {
                throw new IllegalStateException("No handler has been found for JSON: " + json);
            }
            return concreteMessage;
        } catch (JsonProcessingException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Map a message to JSON.
     *
     * @param message
     * @return
     */
    @Override
    public String mapMessageToJson(Message message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a "generic" message (the one that has a type field) from JSON.
     *
     * @param json
     * @return TODO: Consider other name for this method so it doesn't resemble Java generics
     */
    private Message getGenericMessage(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Message.class);
    }

    /**
     * Get a "concrete" message.
     *
     * @param json
     * @param genericMessage
     * @return
     */
    private Message getConcreteMessage(String json, Message genericMessage) throws JsonProcessingException {
        var clazz = MESSAGE_TYPE_MAPPING.get(genericMessage.getType());
        if (clazz == null) {
            return null;
        }
        return objectMapper.readValue(json, clazz);
    }

}
