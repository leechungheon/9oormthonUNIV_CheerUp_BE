# OAuth 로그인 API 사용 가이드

이 문서는 프론트엔드에서 백엔드의 OAuth 로그인 API를 사용하는 방법을 설명합니다.

## 🔗 API 엔드포인트

### 1. 사용자 정보 조회
```
GET /api/users/me
```
현재 로그인된 사용자의 정보(ID, 이메일, 닉네임, OAuth 제공자)를 반환합니다.

**응답 예시:**
```json
{
  "success": true,
  "message": "사용자 정보 조회 성공",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "username": "사용자닉네임",
    "provider": "google"
  }
}
```

### 2. 인증 상태 확인
```
GET /api/users/auth/status
```
현재 사용자의 인증 상태를 확인합니다.

**응답 예시:**
```json
{
  "success": true,
  "message": "인증된 사용자입니다.",
  "data": true
}
```

### 3. 로그아웃
```
POST /api/users/auth/logout
```
JWT 토큰 쿠키를 삭제하고 로그아웃을 처리합니다.

**응답 예시:**
```json
{
  "success": true,
  "message": "로그아웃 성공",
  "data": "로그아웃되었습니다."
}
```

## 🚀 OAuth 로그인 플로우

### 1. OAuth 로그인 시작
사용자가 OAuth 로그인을 원할 때, 다음 URL로 리다이렉트하세요:

```javascript
// Google 로그인
window.location.href = 'https://api.cheer-up.net/oauth2/authorization/google';

// 네이버 로그인
window.location.href = 'https://api.cheer-up.net/oauth2/authorization/naver';

// 카카오 로그인
window.location.href = 'https://api.cheer-up.net/oauth2/authorization/kakao';
```

### 2. OAuth 콜백 처리
OAuth 인증이 성공하면 백엔드는 `/api/users/oauth/callback`으로 리다이렉트합니다.
이 페이지는 자동으로 사용자 정보를 프론트엔드로 전달합니다.

**팝업창으로 로그인하는 경우:**
```javascript
// OAuth 로그인 팝업 열기
const popup = window.open(
  'https://api.cheer-up.net/oauth2/authorization/google',
  'oauth',
  'width=500,height=600'
);

// 메시지 리스너 등록
window.addEventListener('message', (event) => {
  if (event.origin !== 'https://api.cheer-up.net') return;
  
  if (event.data.type === 'OAUTH_SUCCESS') {
    const userInfo = event.data.data;
    console.log('로그인 성공:', userInfo);
    
    // 사용자 정보를 상태에 저장
    setUser(userInfo);
    
    // 팝업 닫기
    popup.close();
  }
});
```

### 3. 인증된 API 호출
로그인 후 API를 호출할 때는 쿠키가 자동으로 포함되므로 추가 헤더가 필요하지 않습니다:

```javascript
// 사용자 정보 조회
const response = await fetch('https://api.cheer-up.net/api/users/me', {
  credentials: 'include' // 쿠키 포함
});

const result = await response.json();
if (result.success) {
  console.log('사용자 정보:', result.data);
}
```

## 🔧 React 예시 코드

### 1. OAuth 로그인 컴포넌트
```jsx
import React, { useEffect, useState } from 'react';

const LoginComponent = () => {
  const [user, setUser] = useState(null);
  // OAuth 로그인 함수
  const handleOAuthLogin = (provider) => {
    const popup = window.open(
      `https://api.cheer-up.net/oauth2/authorization/${provider}`,
      'oauth',
      'width=500,height=600'
    );

    // 메시지 리스너
    const messageListener = (event) => {
      if (event.origin !== 'https://api.cheer-up.net') return;
      
      if (event.data.type === 'OAUTH_SUCCESS') {
        setUser(event.data.data);
        popup.close();
        window.removeEventListener('message', messageListener);
      }
    };

    window.addEventListener('message', messageListener);
  };
  // 사용자 정보 조회
  const fetchUserInfo = async () => {
    try {
      const response = await fetch('https://api.cheer-up.net/api/users/me', {
        credentials: 'include'
      });
      
      const result = await response.json();
      if (result.success) {
        setUser(result.data);
      }
    } catch (error) {
      console.error('사용자 정보 조회 실패:', error);
    }
  };

  // 로그아웃
  const handleLogout = async () => {
    try {
      const response = await fetch('https://api.cheer-up.net/api/users/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });
      
      const result = await response.json();
      if (result.success) {
        setUser(null);
      }
    } catch (error) {
      console.error('로그아웃 실패:', error);
    }
  };

  // 컴포넌트 마운트 시 사용자 정보 확인
  useEffect(() => {
    fetchUserInfo();
  }, []);

  if (user) {
    return (
      <div>
        <h2>환영합니다, {user.username}님!</h2>
        <p>이메일: {user.email}</p>
        <p>로그인 제공자: {user.provider}</p>
        <button onClick={handleLogout}>로그아웃</button>
      </div>
    );
  }

  return (
    <div>
      <h2>로그인</h2>
      <button onClick={() => handleOAuthLogin('google')}>
        Google로 로그인
      </button>
      <button onClick={() => handleOAuthLogin('naver')}>
        네이버로 로그인
      </button>
      <button onClick={() => handleOAuthLogin('kakao')}>
        카카오로 로그인
      </button>
    </div>
  );
};

export default LoginComponent;
```

### 2. 인증 상태 관리 Hook
```jsx
import { useState, useEffect } from 'react';

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  // 인증 상태 확인
  const checkAuthStatus = async () => {
    try {
      setLoading(true);
      const response = await fetch('https://api.cheer-up.net/api/users/auth/status', {
        credentials: 'include'
      });
      
      const result = await response.json();
      if (result.success && result.data) {
        // 인증된 경우 사용자 정보 조회
        await fetchUserInfo();
      }
    } catch (error) {
      console.error('인증 상태 확인 실패:', error);
    } finally {
      setLoading(false);
    }
  };
  // 사용자 정보 조회
  const fetchUserInfo = async () => {
    try {
      const response = await fetch('https://api.cheer-up.net/api/users/me', {
        credentials: 'include'
      });
      
      const result = await response.json();
      if (result.success) {
        setUser(result.data);
      }
    } catch (error) {
      console.error('사용자 정보 조회 실패:', error);
    }
  };

  // 로그아웃
  const logout = async () => {
    try {
      const response = await fetch('https://api.cheer-up.net/api/users/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });
      
      const result = await response.json();
      if (result.success) {
        setUser(null);
      }
    } catch (error) {
      console.error('로그아웃 실패:', error);
    }
  };

  useEffect(() => {
    checkAuthStatus();
  }, []);

  return {
    user,
    loading,
    logout,
    refetch: fetchUserInfo
  };
};
```

## 🌐 환경별 URL 설정

### 개발 환경
- 백엔드: `http://localhost:8080`
- 프론트엔드: `http://localhost:3000`

### 프로덕션 환경
- 백엔드: `https://api.cheer-up.net`
- 프론트엔드: `https://cheer-up.net`

## 🌍 환경별 사용 가이드

### 프로덕션 환경 사용법

프로덕션 환경에서는 다음 URL을 사용하세요:

```javascript
// 프로덕션 환경 설정
const API_BASE_URL = 'https://api.cheer-up.net';
const FRONTEND_URL = 'https://cheer-up.net';

// OAuth 로그인
const handleOAuthLogin = (provider) => {
  window.location.href = `${API_BASE_URL}/oauth2/authorization/${provider}`;
};

// API 호출 시
const response = await fetch(`${API_BASE_URL}/api/users/me`, {
  credentials: 'include'
});
```

### 개발 환경 사용법

개발 환경에서는 다음 URL을 사용하세요:

```javascript
// 개발 환경 설정
const API_BASE_URL = 'http://localhost:8080';
const FRONTEND_URL = 'http://localhost:3000';

// OAuth 로그인
const handleOAuthLogin = (provider) => {
  window.location.href = `${API_BASE_URL}/oauth2/authorization/${provider}`;
};

// API 호출 시
const response = await fetch(`${API_BASE_URL}/api/users/me`, {
  credentials: 'include'
});
```

### 환경 감지 자동 설정

React 앱에서 환경을 자동으로 감지하여 설정하는 예시:

```javascript
// config.js
const isDevelopment = process.env.NODE_ENV === 'development';

export const config = {
  API_BASE_URL: isDevelopment 
    ? 'http://localhost:8080' 
    : 'https://api.cheer-up.net',
  FRONTEND_URL: isDevelopment 
    ? 'http://localhost:3000' 
    : 'https://cheer-up.net'
};

// 사용 예시
import { config } from './config';

const handleOAuthLogin = (provider) => {
  window.location.href = `${config.API_BASE_URL}/oauth2/authorization/${provider}`;
};
```

## ⚠️ 주의사항

1. **CORS 설정**: 백엔드에서 프론트엔드 도메인이 CORS에 허용되어 있는지 확인하세요.
2. **쿠키 설정**: `credentials: 'include'` 옵션을 사용하여 쿠키가 포함되도록 해야 합니다.
3. **HTTPS**: 프로덕션 환경에서는 반드시 HTTPS를 사용해야 합니다.
4. **보안**: JWT 토큰은 HttpOnly 쿠키로 관리되므로 JavaScript에서 직접 접근할 수 없습니다.
5. **Secure 플래그**: 프로덕션 환경(cheer-up.net)에서는 쿠키에 Secure 플래그가 자동으로 설정됩니다.
6. **SameSite**: 크로스 사이트 요청으로부터 보호하기 위해 적절한 SameSite 정책을 사용합니다.

## 🔍 디버깅

인증 관련 문제가 발생할 경우:

1. 브라우저 개발자 도구에서 Network 탭을 확인하여 API 요청/응답을 확인하세요.
2. Application 탭에서 쿠키가 제대로 설정되었는지 확인하세요.
3. 백엔드 로그를 확인하여 JWT 토큰 생성/검증 과정을 추적하세요.
