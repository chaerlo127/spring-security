# Spring Security & JWT
이 레포지토리는 Spring Security 프레임워크 및 jwt 기술을 이용하여 간단한 인증 및 인가 기능을 구현한 프로젝트입니다.
<hr>

## 1. Spring Security란?
스프링 기반의 애플리케이션의 인증, 인가와 같은 보안을 담당하는 스프링 하위 프레임워크이다. 

## 2. Security 용어
Spring Security는 보안과 관련된 프레임워크이다보니 보안과 관련된 용어들이 많이 나온다. Spring Security를 이해하기 위해서는 보안 관련 용어를 익히는 것이 중요하다.

| 단어                 | 설명                                                  |
|--------------------|-----------------------------------------------------|
| 인증(Authentication) | 보호된 리소스에 접근한 대상이 누구인지, 애플리케이션에 접근해도 되는 주체인지 확인하는 과정 |
| 인가(Authorize)      | 해당 리소스에 대해 접근을 갖고 있는 대상인지 확인하는 과정 → 인증 이후에          |
| 접근 주체(Principal)   | 보호된 리소스에 접근하는 대상                                    |
| 권한                 | 리소스에 대한 접근 제한                                       |

즉, 로그인을 진행하는 과정 속에서 애플리케이션을 접근할 수 있는 주체인지 확인하는 과정이 이루어지는 것이 **인증(Authentication)** 이며, 로그인 후에 사용이 가능한 기능들(댓글, 좋아요 등등)에 접근할 수 있는지 없는지 토큰을 통해 확인하는 과정이 **인가(Authorize)** 이다. 

## 3. Servlet Filter
Spring Security는 Servlet Filter를 기반으로 보안을 지원한다.
보안 인증 부분을 각각의 Controller에서 인가를 위해 컨트롤러 메서드의 첫 부분마다 인증 코드를 작성할 수 있다. 하지만, 이 경우에는 API 가 많아질수록 중복 코드가 많이 발생하게 되고, 이를 해결하기 위해 **Servlet 필터**를 사용한다.

```text
💡 Servlet이란 동적 웹 페이지를 만들 때 사용되는 자바 기반의 웹 어플리케이션으로, 서버에서 실행되다가 웹 브라우저에서 요청하면 해당 기능을 수행한 후 웹 브라우저에 결과를 전송한다.

```
Servlet Filter는 Client로 부터 Server로 요청이 들어오기 전에 Servlet을 거쳐서 필터링을 진행한다. 즉, **Servlet Filter는 Servlet이 실행전에 항상 실행되는 클래스** 이며, 개발자가 필터를 상속받아 어플리케이션에 맞게 Filter를 구현하고, Servlet Filter를 Servlet 컨테이너가 실행하도록 설정해준다.

<br>

사용자의 요청(Request)이 Servlet에 전달되기 전 Filter를 통해서 인증과 인가에 대한 작업을 수행할 수 있지만, Spring Container에 등록된 Bean은 인식할 수 없다. 이때, Spring Securtiy에서는 Servlet 사이에 FilterChainProxy라는 Filter를 통해서 Spring에 등록된 Bean을 가져와서 의존성을 주입한다.

### **FilterChainProxy**
![filterChainProxy](https://user-images.githubusercontent.com/90203250/222059658-dd9e23c1-567e-43f4-a3fa-e15aa22db258.png)

**FilterChainProxy**는 **DelegatingFilterProxy**를 통해 받은 요청과 응답을 Filter Chain에 전달하고 작업을 위임하는 역할을 한다.

**DelegatingFilterProxy**에서 바로 Security Filter Chain을 실행할 수 있지만, 서블릿을 지원하는 시작점을 알고 서블릿에 문제가 발생하면 **FilterChainProxy** 라는 것을 바로 알기 위해 **FilterChainProxy**을 사용한다. 

### Security Filter Chain
![SecurityFilterChain](https://user-images.githubusercontent.com/90203250/222059752-59aac4e8-6de9-4907-9ac2-bf36f81d0ad9.png)

인증을 처리하는 여러개의 Security Filter를 담고 있는 필터 Chain (연결고리)로, **FilterChainProxy**를 통해 Servelt Filter와 연결이 되고, 어떤 Security Filter를 통해 인증, 인가를 할지 결정하는 역할을 한다.

또한, 그림에서 볼 수 있듯이 한 프로젝트 내에서 여러 Security Chain을 가질 수 있다.

### Security Filter

요청을 Spring Security 매커니즘에 따라 핵심 기능을 수행하는 필터이다. Security Filter는 SecurityFilterChain API를 통해 FilterChainProxy에 삽입되고 스프링 빈으로 등록된다.

Security Filter는 프로젝트에서 상속을 받아 사용하며, 개발자는 프로젝트의 성격에 맞게 변경하여 사용을 할 수 있다. 다음은 Security Filter의 종류이다. Security Filter는 순서가 존재한다. 종류와 순서는 아래 링크를 통해 확인이 가능하다.

[https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#servlet-security-filters](https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#servlet-security-filters)

간단하게 Suvlet Filter에 대해 요약하자면 사용자에게 요청을 받아 Servlet 내에서 Filter를 실행하고 연결된 다음 Filter(Next Filter)에 전달하여 기능을 진행한다. 이때, Filter Chain 내 DelegatingFilterProxy의 FilterChainProxy와 연결하여 Spring의 Bean 의존성을 주입받은 SecurityFilterChain 내 Filter들이 차례대로 실행되면서 인증, 인가 역할을 진행하는 것이다.

## 4. JWT (Json Web Token)

JWT는 RFC7519 웹 표준으로 JSON 객체를 이용해 데이터를 주고받을 수 있도록한 웹 토큰이다.

![jwt](https://user-images.githubusercontent.com/90203250/222059858-722e294b-7c1c-4b51-9e2f-9fcc3dfb2af9.png)

header, payload, signature로 구성되어 있으며, 실제 데이터들은 payload에 저장되어 있다. signature에서는 토큰의 유효성을 검증하기 위함이고, header는 signature를 해싱하기 위한 알고리즘 정보가 담겨 있다. 


 <details><summary>출처</summary>

https://limdevbasic.tistory.com/19

https://junseokdev.tistory.com/48

https://velog.io/@falling_star3/Tomcat-%EC%84%9C%EB%B8%94%EB%A6%BFServlet%EC%9D%B4%EB%9E%80

https://imbf.github.io/spring/2020/06/29/Spring-Security-with-JWT.html

https://velog.io/@jsj3282/Spring-Security-%EC%A0%95%EB%A6%AC1

https://velog.io/@suhongkim98/Spring-Security-JWT%EB%A1%9C-%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
</details>
