# GreenMate

![그린메이트_포스터](https://github.com/user-attachments/assets/36ac475f-4b93-4a2d-80d3-15ae227b3e4c)


## Introduction
<pre>
2024학년도 캡스톤 디자인 프로젝트입니다.
바쁜 현대인들이 반려식물을 더욱 쉽고 편리하게 관리할 수 있도록 실시간 모니터링과
맞춤형 케어 가이드를 제공하여 초보 식집사도 성공적으로 반려 식물을 키울 수 있게 도와주는
Android Native 앱입니다.
</pre>
***

## 주요기능
- 반려식물 종 분류 : 딥러닝으로 학습한 반려식물 종 분류 모델로 반려식물을 카메라로 인식하여 종 분류
- 질병 분류 : 질변 분류 모델로 반려식물 질병 부위를 카메라로 인식하여 질병 분류
- 일지 작성 : 반려식물에 관한 일지를 작성
- 아두이노와 블루투스 연결 : 반려식물을 관찰하기 위해 아두이노와 저전력 블루투스 연결
- 반려식물 관찰 및 차트 : 반려식물에 관한 공기 중 온도, 습도, 광량, 그리고 토양 수분 데이터를 모니터링. 모니터링한 데이터들을 차트로 나타냄
- 반려식물 물 주기 및 양 알림 : 식물 별로 필요한 물의 양과 주기를 아두이노 토양 수분 모듈과 매칭하여 모니터링한 값이 반려식물이 물을 필요로하면 알림

## 사용기술
- 앱 개발: Android Studio
- 언어: Kotlin
- 앱 화면 제작: Compose

## Android Stack
- hilt
- room
- coil
- coroutines
- gson
- tensorflow-lite
- jetpack library 등

## ERD
<img width="650" alt="image" src="https://github.com/user-attachments/assets/e9fcc0e0-c21c-498d-8710-03734464fa65">
