import React from 'react';
import ReactDOM from 'react-dom/client';
import MapContainer from './map'; // 수정: map.js 파일을 정확한 이름으로 import

const root = document.getElementById('root');

ReactDOM.createRoot(root).render( // 수정: createRoot 메서드를 호출하고 render 메서드에 JSX를 전달
  <React.StrictMode>
    <MapContainer /> {/* 수정: map.js 파일의 컴포넌트를 JSX로 사용 */}
  </React.StrictMode>
);