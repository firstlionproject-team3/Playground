export const validation = {
  loginId: (value: string): string | null => {
    if (!value) return '로그인 ID를 입력해주세요.';
    if (value.length > 100) return '로그인 ID는 100자 이하여야 합니다.';
    if (!/^[a-zA-Z0-9]+$/.test(value)) return '로그인 ID는 영문과 숫자만 사용 가능합니다.';
    return null;
  },

  password: (value: string): string | null => {
    if (!value) return '비밀번호를 입력해주세요.';
    if (value.length < 8 || value.length > 64) {
      return '비밀번호는 8자 이상 64자 이하여야 합니다.';
    }
    if (/\s/.test(value)) return '비밀번호에 공백을 포함할 수 없습니다.';
    if (!/[a-zA-Z]/.test(value)) return '비밀번호에 영문이 포함되어야 합니다.';
    if (!/[0-9]/.test(value)) return '비밀번호에 숫자가 포함되어야 합니다.';
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(value)) {
      return '비밀번호에 특수문자가 포함되어야 합니다.';
    }
    return null;
  },

  email: (value: string): string | null => {
    if (!value) return null; // optional
    if (value.length > 100) return '이메일은 100자 이하여야 합니다.';
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) return '올바른 이메일 형식이 아닙니다.';
    return null;
  },

  title: (value: string): string | null => {
    if (!value || value.trim().length === 0) return '제목을 입력해주세요.';
    return null;
  },

  content: (value: string): string | null => {
    if (!value || value.trim().length === 0) return '내용을 입력해주세요.';
    return null;
  },
};

