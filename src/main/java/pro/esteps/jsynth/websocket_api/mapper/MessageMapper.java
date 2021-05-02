package pro.esteps.jsynth.websocket_api.mapper;

import pro.esteps.jsynth.messaging.message.Message;

/**
 * Message <-> JSON mapper.
 */
public interface MessageMapper {

    /**
     * Map JSON to a message.
     *
     * @param json
     * @return
     */
    Message mapJsonToMessage(String json);

    /**
     * Map a message to JSON.
     *
     * @param message
     * @return
     */
    String mapMessageToJson(Message message);

}
