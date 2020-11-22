# Java NIO Chatting-app

## 프로젝트 상세
* 구현 기술
	* Java 12 
		* ExecutorService, CompletableFuture로 비동기 구현
		* Jave Reflection API, 애너테이션을 이용한 메서드 매핑(@RequestMapping 애너테이션을 이용한 작업 분기 처리)
		* Selector, Buffer 와 같은 NIO  API를 이용해 이벤트 방식의 메세지 처리 구현
	
	* 구현 기능
		* 채팅방 생성
			* 채팅방 생성 시 최대 인원을 지정할 수 있다.
		* 생성된 채팅방 리스트 조회
		* 채팅방 나가기
		* 채팅 기능
			* !exit 를 입력하면 채팅방을 나갈 수 있다.

## Architecture
example case : 채팅방 생성

![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://blog.kakaocdn.net/dn/wVZQa/btqNWfXA48W/WdJYIjqc6I9LS4K39VR960/img.png)

## 비디오
아래의 링크로 간단한 시연 영상을 볼 수 있습니다.
[https://youtu.be/omo-jDPEKKc](https://youtu.be/omo-jDPEKKc)



## 버그
* 채팅방을 만들고 최초로 대화할 때, 메세지가 안 보내지는 현상.

