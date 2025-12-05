package shop.chaekmate.api.event;

import java.io.Serializable;

public record MemberSignedUpEvent(
        Long memberId
) implements Serializable {
}
