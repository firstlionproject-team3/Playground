export const validateLoginId = (loginId: string): string | null => {
  if (!loginId) {
    return '아이디를 입력해주세요.';
  }
  if (!/^[a-zA-Z0-9]+$/.test(loginId)) {
    return '아이디는 영문과 숫자만 사용할 수 있습니다.';
  }
  if (loginId.length > 100) {
    return '아이디는 100자 이하여야 합니다.';
  }
  return null;
};

export const validatePassword = (password: string): string | null => {
  if (!password) {
    return '비밀번호를 입력해주세요.';
  }
  if (password.length < 8 || password.length > 64) {
    return '비밀번호는 8자 이상 64자 이하여야 합니다.';
  }
  if (!/^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).+$/.test(password)) {
    return '비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.';
  }
  if (/\s/.test(password)) {
    return '비밀번호에 공백을 사용할 수 없습니다.';
  }
  return null;
};

export const validateEmail = (email: string): string | null => {
  if (!email) {
    return null; // 이메일은 선택사항
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return '올바른 이메일 형식이 아닙니다.';
  }
  if (email.length > 100) {
    return '이메일은 100자 이하여야 합니다.';
  }
  return null;
};

export const validateNickname = (nickname: string): string | null => {
  if (!nickname) {
    return '닉네임을 입력해주세요.';
  }
  if (nickname.length < 2 || nickname.length > 20) {
    return '닉네임은 2자 이상 20자 이하여야 합니다.';
  }
  return null;
};

export const validateContent = (content: string, fieldName = '내용'): string | null => {
  if (!content || content.trim().length === 0) {
    return `${fieldName}을(를) 입력해주세요.`;
  }
  return null;
};

export const validateTitle = (title: string): string | null => {
  if (!title || title.trim().length === 0) {
    return '제목을 입력해주세요.';
  }
  if (title.length > 200) {
    return '제목은 200자 이하여야 합니다.';
  }
  return null;
};

