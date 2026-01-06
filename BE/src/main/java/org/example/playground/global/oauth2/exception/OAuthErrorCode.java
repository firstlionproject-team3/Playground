package org.example.playground.global.oauth2.exception;

import lombok.Getter;

@Getter
public enum OAuthErrorCode {

    ATTRIBUTES_MAPPING_FAILED(
            "attributes_mapping_failed",
            "OAuth provider 응답에서 사용자 정보(attributes)를 매핑하지 못했습니다.",
            null
    ),

    PROVIDER_ID_MAPPING_FAILED(
            "provider_id_mapping_failed",
            "OAuth provider 응답에서 사용자 식별자(providerId)를 매핑하지 못했습니다.",
            null
    ),

    UNSUPPORTED_PROVIDER(
            "unsupported_provider",
            "지원하지 않는 OAuth provider 입니다.",
            null
    ),
    ;

    private final String code;
    private final String description;
    private final String uri; //에러 설명 문서 링크

    OAuthErrorCode(String code, String description, String uri) {
        this.code = code;
        this.description = description;
        this.uri = uri;
    }

    public static OAuthErrorCode from(String code) {
        if (code == null) { return null; }

        for (OAuthErrorCode oAuthErrorCode : OAuthErrorCode.values()) {
            if (code.equals(oAuthErrorCode.code)) {
                return  oAuthErrorCode;
            }
        }

        return null;
    }


}
