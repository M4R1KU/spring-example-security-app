# Modul 183
## Florian Bürgi & Mario Kunz

## Inhalt

[TOC]

## Bausteine
In den folgenden Unterkapiteln wird der verwendete Technologie Stack erklärt. Die Gesamtarchitektur richtet sich nach MVC, daher besteht die Applikation auch aus drei Bausteinen. 
### H2
Als Datenbank wird die H2 Database Engine verwendet.

H2 kann direkt in die Anwendung eingebunden werden und muss, daher nicht auf dem Server installiert werden.
Zudem ist das ganze System sehr schlank und effizient gebaut, was für eine erhöhte Performance sorgt.
H2 besitzt aufgrund der Schlankheit einige Features von anderen Datenbankmanagementsystemen nicht, wie zum Beispiel "Stored Procedures/Functions".
Dies hat aber keinen Einfluss auf das vorliegende Projekt, da auf diese Funktionalitäten verzichtet werden kann.

H2 kann wie schon erwähnt direkt in die Applikation eingebunden werden, dies passiert über die Gradle Konfiguration.
```groovy
runtime('com.h2database:h2')
```
Im Normalmodus entspricht H2 dann einer In-Memory Datenbank, was heisst, dass die Daten nach dem Neustart der Applikation verloren gehen.
Um diesem Verhalten entgegenzuwirken wird im JDBC-String explizit `file` und der Pfad zur Datei (hier im Home-Verzeichnis des Users) angegeben.
```
jdbc:h2:file:~/securityapp
```

### Spring
Spring bzw. Spring Framework ist ein Open-Source Java Framework und kann unteranderem zur Entwicklung von Webapplikation verwendet werden.

Wir verwenden zudem Spring Boot. Spring Boot macht es einfach mit der riesigen Spring Platform zu starten.
Durch sogenannte Starter können verschiedenste Abhängigkeiten zu der Spring Platform (Web, Data, etc.) auf eine einfache Weise realisiert werden.
Die Starter sind immer mit Standardkonfigurationen erstellt, damit eine Spring Boot Applikation theoretisch ohne jeweilige Konfiguration lauffähig wäre.
Dies alles führt dazu, dass Spring Boot Applikationen um einiges eifnacher zum Einrichten sind und man sich auf das wichtige konzentrieren kann. Zudem vermindert man die Konfiguration, indem man nur das nötige bzw. dort wo man vom Standard abweicht konfiguriert.

### Thymeleaf

Das Frontend für unsere Applikation wird mit der Thymeleaf Templating Engine erstellt. Thymeleaf ist eine moderne Templating Engine für Java und quasi ein Nachfolger von JSP oder JSF.

Thymeleaf integriert sich sehr gut in Spring und es bietet zudem auch schon einige Sicherheitsmechanismen. So wird zum Beispiel jeder Text standardmässig bei der Anzeige escaped. So kann XSS verhindert werden.

## Spring Security Konfiguration

Für die Umsetzung der Sicherheits-Anforderungen wird Spring Security verwendet. Spring Security unterstützt out-of-the-box schon sehr viele Sicherheitsmechanismen, wie auch zum Beispiel das erforderliche "salten" von Passwörtern. Auch das Session-Management wird von Haus aus unterstützt und passiert automatisch.

Die Konfigurationseinstellungen werden in den folgenden Unterkapiteln erklärt.

Untenstehend ist die komplette `SecurityConfig` Klasse zu sehen. Dies ist der Ort wo der grösste Teil der Konfiguration vorgenommen wird.

```kotlin
@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsService,
                     val rateLimitHolder: RateLimitHolder) : WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun rateLimitingFilter(rateLimitHolder: RateLimitHolder): FilterRegistrationBean<RateLimitingFilter> {
        val registrationBean = FilterRegistrationBean<RateLimitingFilter>(RateLimitingFilter(rateLimitHolder))
        registrationBean.addUrlPatterns("/do-login/*")
        registrationBean.order = 5
        return registrationBean

    }

    override fun configure(web: WebSecurity?) {
        web!!.ignoring().antMatchers("/css/**", "/h2-console/**")
    }

    override fun configure(http: HttpSecurity?) {
        http!!.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .usernameParameter("user").passwordParameter("pass")
                .loginProcessingUrl("/do-login")
                .successHandler { request, response, _ ->
                    rateLimitHolder.clear(request.requestAddress)
                    response.sendRedirect("/")
                }
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(userDetailsService)
    }
}
```

### Basis

Damit Spring Security überhaupt aktiviert wird, muss die `@EnableWebSecurity` Annotation an einer Klasse angehängt werden. Diese aktiviert wie schon erwähnt den ganzen Spring Security Stack und markiert unter anderem die aktuelle Klasse als Konfigurationsklasse, was uns dann ermöglicht mit `@Bean` eigene Beans für die Dependency Injection zu erstellen.

Nebst dem Anfügen der `@EnableWebSecurity` Annotation erbt die aktuelle Klasse auch noch von der Klasse `WebSecurityConfigurerAdapter`, welche es uns ermöglicht das Standardverhalten vom Security Stack zu konfigurieren.
Die Einzelheiten dazu werden noch erläutert.

### Beans

Unsere `SecurityConfig` definiert zwei Beans. Zum einen haben wir einen `PasswordEncoder` und eine `FilterRegistrationBean`.

#### PasswordEncoder

Damit die Passwörter beim Speichern gehasht werden und auch richtig verglichen werden (beim Login) definieren wir den `BCryptPasswordEncoder` als unseren PasswordEncoder. Unteranderem unterstützt dieser Encoder das "salten" von Passwörtern, was aber im Kapitel Passwort-Persistierung noch genauer erklärt wird.

#### FilterRegistrationBean

Die `FilterRegistrationBean`  ist eigentlich nur eine Wrapper-Klasse für einen standardmässigen Filter.

Was ist ein Filter überhaupt? Spring Web enthält eine Implementation des Chain-of-Responsibility-Patterns, welche jede Request, welche zum Server kommt durch eine Reihe von Filtern laufen lässt. Die `FilterRegistrationBean` ist ein Weg um einen Filter zu registrieren. Zudem bietet es uns die Möglichkeit den Filter nur auf bestimmte URL's zu setzen. Wir setzen zudem noch den Standort in der Filter-Chain damit wir sichergehen, dass unser Filter vor der `springSecurityFilterChain` läuft, da diese jenachdem Weiterleitungen durchführt und somit unseren eigenen Filter aus der Chain kickt.

Den Filter, den wir Registrieren, ist der `RateLimitingFilter`, welcher die Anzahl Aufrufe pro Zeit für einen bestimmten Anfrager limitiert. Die Funktionsweise des Filters wird in einem späteren Kapitel erklärt.

### WebSecurity

In der Funktion `configure(web: WebSecurity?)` sagen wir Spring Security auf welchen URL's es keine Sicherheitsmechanismen aktivieren muss. Dies ist bei uns die URL wo wir die CSS-Dateien für die Darstellung holen und zudem noch die Adresse der H2-Konsole, über die wir einen Einblick in die Datenbank erhalten können. Dieser Endpoint hat ein eigenes Login und wird für die Produktion noch deaktiviert.

### HttpSecurity

In der Funktion `configure(http: HttpSecurity?)` passiert eigentlich das Spannende. Hier wird nämlich das Verhalten von Spring Security im Bezug auf Authentifizierung konfiguriert.

Mit `anyRequest().authenticated()` geben wir an, dass wir keine Public URL's haben und dass wir für alle URL's eine Authentifizierung erfordern.

Mit `formLogin()` aktivieren die Art des Logins und zwar ist es ein Login über ein Formular. Man könnte hier ein Standardformular von Spring generieren lassen, jedoch ist dieses in keiner Form mit CSS gestylt und daher erstellten wir unser eigenes im Pfad `/login` (`loginPage("/login")`). Damit die Login-Maske auch zugänglich für User ohne Authentifizierung ist geben, wir es hier noch explizit mit `permitAll()` an.

Da die Anforderungen es erfordern, dass die Anmeldung über die zwei Parameter "user" und "pass" gehen, weichen wir von den Standards "username" und "password" ab und müssen diese auf der nächsten Zeile noch explizit angeben.

Als finaler Schritt geben wir eine URL an wo das Login auf der Serverseite verarbeitet werden soll. Dies erstellt einen neuen POST Endpoint, welcher die beiden oben angegebenen Parameter verarbeitet. Die Verarbeitungs-URL muss hier von der Anzeige URL abweichen, damit wir den `RateLimitingFilter` nur auf die Verarbeitung setzen können. Aus diesem Grund passiert die Verarbeitung auf `/do-login`.

Als letztes erstellen wir noch einen `SuccessHandler`, welcher nach jedem erfolgreichen Login läuft. Darin räumen wir den RateLimitingFilter auf. Mehr dazu im nächsten Kapitel.
Damit wir dann auf der Startseite für die eingeloggten User landen müssen wir noch ein Redirect durchführen.

### UserDetailsService

In der letzten Methode der Klasse `configure(auth: AuthenticationManagerBuilder?)` geben wir Spring an von wo es unsere User bekommt. Dafür haben wir eine Implementation des `UserDetailsService`, welcher wir hier registrieren.

Das `UserDetailsService` Interface enthält eine abstrakte Methode namens `loadUserByUsername`, welche einen Username als Parameter nimmt und ein UserDetails Objekt zurückgibt.

Da unsere User in der Datenbank gespeichert werden, greifen wir über das UserRepository auf diese zu und erstellen aus der Entity ein User Objekt. Die Klasse User ist eine einfache, von Spring mitgebrachte, Implementation des UserDetails Interface und enthält einen Benutzernamen, ein Passwort und eine Liste von Authorities bzw. Rollen. Da wir keine Rollen verwenden, geben wir nur eine leere Liste an.

```kotlin
@Component
class DatabaseUserDetailsService(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username!!) ?: 
        	throw UsernameNotFoundException(username)
        return User(user.username, user.password, listOf())
    }
}
```

## Login und Session Management

Das Login wird wie bereits erwähnt durch eine Standard-Spring Funktionalität abgehandelt. 
Die SecurityConfig erzwingt, dass der Benutzer authentifiziert sein muss um auf um auf die verschiedenen Masken zugreifen kann.
Lediglich der Zugriff auf die Login-Maske bleibt gestattet.
Versucht ein nicht authentifizierter Benutzer auf eine der "gesicherten" Masken zuzugreifen, so wird er durch einen Filter an die Loginseite weitergeleitet.

Meldet sich ein User erfolgreich an so wird eine (neue) JSESSIONID erstellt und in einem Cookie gespeichert.
Das Cookie wird lediglich über HTTPS übertragen um Session Hijacking zu erschweren.

## Registrierung

Damit ein Benutzer einen anderen Benutzer registrieren kann, muss er eingeloggt sein.
Die Eingaben werden dahingehend validiert, dass der Benutzername eindeutig sein muss, das Passwort gewissen Komplexitätsanforderungen entspricht und die beiden Passwort-Felder übereinstimmen.
Die Daten werden via HTTPS übertragen und das Passwort wie beschriben gehashed.

### Passwort-Persistierung

Die im Kapitel PasswordEncoder beschriebene Klasse `BCryptPasswordEncoder`  wird verwendet um aus dem Plaintextpasswort des Users einen sicheren Hash zu generieren.

Der `BCryptPasswordEncoder`  generiert einen Hash, welcher wie folgt aussehen könnte:

`$2a$10$2TjFmQuJ56Qo0hPoLGjLP.sQ1x/tt/Z19o7FilL1toX/RMLPobXAu  `

Die `$` Zeichen trennen die drei Haupteile der 4 Passwortbestandteile:

1. Der Hash-Algorithmus: 

   ```
   1	= 	md5
   2a 	= 	bcrypt
   5	= 	sha-256   
   ```

2. Der Kostenfaktor der Passwortberechnung (Teil des Brute-Force Schutzes)

3. Der Base64 encoded String aus Salt und Passwort-Hash

   1. Die ersten 22 Zeichen stehen für das Salt
   2. Der Rest für den Hash des eigentlichen Passworts

   Dabei gilt es zu beachten, dass der Base-64 String auf einem speziellen Zeichensatz basiert.
   Der verwendete Hash-Alogrithmus ist bewusst langsam und die Berechnungskosten erhöhen sich nach wiederholten Berechnungen (Blowfish).

## SSL
Spring Boot unterstüzt das Einbinden von SSL Zertifikaten via die Konfigurationsdatei `application.yml`.
Alle vom Framework erstellten Webservice-Endpoints basieren dann auf mit dem Zertifikat abgsicherten WebSocket.

Beispiel Konfiguration:

```yaml
server:
  port: 8443
  ssl:
    key-store: school_security_application_keystore.jks
    key-store-password: **store_pw** 
    keyAlias: school_security_certificate
    key-store-type: JKS
    key-password: **key_pw**
```

Alternativ könnte über eine Verschlüsselung auf Reverse-Proxy Ebene nachgedacht werden.

## Systemkommando

Für die Ausführung von Systemkommandos bietet sich im Java Umfeld die Verwendung des ProcessBuilders an. 
Die Verwendung dieses Builders bietet zwei wesentliche Vorteile:
1. Die Plattformunabhängigkeit bleibt bestehen. Solange das übergebene Kommando im aktuellen Systemkontext ausführbar ist, sind keine weiteren Massnahmen nötig.
2. Trennung von Kommando und Argumenten
Die ProcessBuilder-Bibliothek trennt beim erstellen eines Prozesses automatisch zwischen den dem Kommando und den dazugehörigen Argumenten.
Dadurch wird sichergestellt, dass via die Parametereingabe kein Übergriff in die Kontrollebene geschehen kann.

Um das System zusätzlich abzusichern, werden alle Parameter durch eine Escaping-Library in "ungefährlichen" Text übersetzt bevor diese an den oben beschriebenen ProcessBuilder übergeben werden.

## Brute-Force Schutz

Als Schutzmassnahme gegen Brute-Force-Angriffe wird ein selbstgebauter Filter eingesetzt.
Der Filter liegt vor der Login-Maske und speichert die IP-Adresse und die Zeit jedes Zugriffs.
Falls von einer IP während einer gewissen Zeit zu viele Requests an die Login-Schnittstelle gesendet werden, wird diese IP auf eine Blockingliste gesetzt und von da an für eine gewisse Zeit alle Requests von dieser IP Adresse ignoriert.
Nach jedem erfolgreichen Login werden die Einträge der jeweiligen IP durch den SuccessHandler, welcher in der SecurityConfig eingerichtet wurde gelöscht, da wir ab diesem Zeitpunkt dieser IP vertrauen.

Die Funktionsweise des Filters ist eher rudimentär und auch nicht wirklich produktionsbereit, da es relativ einfach ist eine IP zu fälschen, jedoch zeigt er die Grundfunktion des Blocken relativ gut und dient zu Demozwecke genügend.

Wichtige Schritte im Durchlaufen des Filters werden geloggt. So zum Beispiel, wenn eine IP geblockt wird.

Der untenstehende Code zeigt unser Vorgehen:
```kotlin
class RateLimitingFilter(val rateLimitHandler: RateLimitHolder) : GenericFilterBean() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            val remoteAddr = request.requestAddress
            if (rateLimitHandler.checkLimitExceeded(remoteAddr)) {
                response.sendRedirect("/login?error")
                return
            }
        }
        chain.doFilter(request, response)
    }
}
```

```kotlin
@Component
class RateLimitHandler {
    companion object {
        const val TIMEFRAME_MINUTES = 5L
        const val REQUESTS_PER_TIMEFRAME = 10
        const val BLOCKED_HOURS = 1L

        private val IP_ACCESS_MAP: MutableMap<String, MutableList<LocalDateTime>> = mutableMapOf()
        private val BLOCKED_IPS: MutableMap<String, LocalDateTime> = mutableMapOf()

        private val LOGGER = LoggerFactory.getLogger(RateLimitHandler::class.java)
    }

    fun checkLimitExceeded(ipAddress: String): Boolean {
        if (ipAddress.isEmpty()) {
            return false
        }
        if (isBlocked(ipAddress)) {
            LOGGER.warn("Blocked IP $ipAddress attempted an access")
            return true
        }
        val now = LocalDateTime.now()
        val lastAttempts = IP_ACCESS_MAP[ipAddress]
        lastAttempts?.removeAll(isInTimeFrame(now))

        val hasExceeded = lastAttempts?.size ?: 0 >= REQUESTS_PER_TIMEFRAME
        if (hasExceeded) {
            LOGGER.warn("Block IP $ipAddress for $BLOCKED_HOURS hours")
            IP_ACCESS_MAP.remove(ipAddress)
            BLOCKED_IPS[ipAddress] = now
        } else {
            IP_ACCESS_MAP.compute(ipAddress) { _, attempts ->
                val attemptsOrEmpty = attempts ?: mutableListOf()
                attemptsOrEmpty.add(now)
                attemptsOrEmpty
            }
        }
        return hasExceeded
    }

    fun clear(ipAddress: String) {
        LOGGER.info("Removing IP $ipAddress from access map")
        IP_ACCESS_MAP.remove(ipAddress)
    }

    private fun isInTimeFrame(now: LocalDateTime): (time: LocalDateTime) -> Boolean {
        return { time ->
            time.isBefore(now.minusMinutes(TIMEFRAME_MINUTES))
        }
    }

    private fun isBlocked(ipAddress: String): Boolean {
        return BLOCKED_IPS[ipAddress]?.isAfter(LocalDateTime.now().minusDays(BLOCKED_HOURS)) ?: false
    }
}
```

Der Filter funktioniert auch für Clients, welche sich hinter einem Proxy verbergen, da wir den `X-Forwarded-For` Header beim ermitteln der IP-Adresse  beachten.

Dies wurde mit einem Extension Property auf dem ` HttpServletRequest` Objekt gemacht.

```kotlin
val HttpServletRequest.requestAddress: String
    get() {
        val forwardedHeader = getHeader("X-FORWARDED-FOR")
        if (forwardedHeader.isNullOrEmpty()){
            return remoteAddr
        }
        return forwardedHeader
    }
```

## Log

Spring verfügt über vordefinierte Logebenen für verschiedene Applikationsteile.
Im Sourcecode kann mit einfachsten Mitteln in dieses Log geschriben werden.

```kotlin
val LOGGER = LoggerFactory.getLogger(SystemCommandService::class.java)
LOGGER.info("Executing system command: $command with parameters: ${argumentArray.joinToString(" ")}")
```
Dieses Codestück generiert einen Log-Eintrag der wie folgt aussehen könnte:
`2018-06-12 19:34:44.211  INFO 1632 --- [nio-8443-exec-7] m.m.g.m.s.service.SystemCommandService   : Executing system command: java with parameters: -version`

Die folgende Konfiguration schreibt die gewünschten Logs an den entsprechenden Pfad:

```yaml
logging:
  file: /var/log/m183-security-app.log
  level:
    root: INFO
    org:
      springframework:
        web: INFO
    hibernate: ERROR
```

