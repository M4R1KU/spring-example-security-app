# Modul 183
## Florian Bürgi & Mario Kunz

## Inhalt
1. Bausteine
      1.1. H2
    1.2. Spring
    1.3. Thymeleaf
2. Datenstruktur
3. Spring Security Konfiguration
4. Passwort-Persistierung
5. Login
6. SSL
7. Systemkommando
8. Brute-Force Schutz
9. Log

## Bausteine
In den folgenden Unterkapiteln wird der verwendete Technologie Stack erklärt. Die Gesmatarchitektur richtet sich nach MVC, daher besteht die Applikation auch aus drei Bausteinen. 
### H2
Als Datenbank wird die H2 Database Engine verwendet.

H2 kann direkt in die Anwendung eingebunden werden und muss, daher nicht auf dem Server installiert werden.
Zudem ist das ganze System sehr schlank und effizient gebaut, was für eine erhöhte Performance sorgt.
H2 besitzt aufgrund der Schlankheit einige Features von anderen Datenbankmanagementsystemen nicht, wie zum Beispiel "Stored Procedures/Functions".
Dies hat aber keinen Einfluss auf das vorliegende Projekt, da auf diese Funktionalitäten verzichtet werden kann.

H2 kann wie schon erwähnt direkt in die Applikation eingebunden werden, dies passiert über die Gradle Konfiguration.
```
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

## Datenstruktur

## Spring Security Konfiguration

## Login

## SSL
Spring Boot unterstüzt das Einbinden von SSL Zertifikaten via die Konfigurationsdatei `application.yml`.
Alle vom Framework erstellten Webservice-Endpoints basieren dann auf mit dem Zertifikat abgsicherten WebSockets.

Beispiel Konfiguration:

```
server:
  port: 8443
  ssl:
    key-store: school_security_application_keystore.jks
    key-store-password: youd_like_to_know_right 
    keyAlias: school_security_certificate
    key-store-type: JKS
    key-password: youd_like_to_know_right 
```

Alternativ könnte über eine Verschlüsselung auf Reverse-Proxy Ebene nachgedacht werden.

## Systemkommando

Für die Ausführung von Systemkommandos bietet sich im Java Umfeld die Verwendung des ProcessBuilders an. 
Die Verwendung dieses Builders bietet zwei wesentliche Vorteile:
1. Die Plattformunabhängigkeit bleibt bestehen. Solange das übergebene Kommando im aktuellen Systemkontext ausführbar ist, sind keine weiteren Massnahmen nötig.
2. Trennung von Kommando und Argumenten
Die ProcessBuilder-Bibliothek trennt beim erstellen eines Prozesses automatisch zwischen den dem Kommando und den dazugehörigen Argumenten.
Dadurch wird sichergestellt, dass via die Parametereingabe kein Übergriff in die Kontrollebene geschehen kann.

Um das System zusätzlich abzusichern, werden alle Parameter durch eine Escaping-Libraray in "ungefährlichen" Text übersetzt bevor diese an den oben beschriebenen ProcessBuilder übergeben werden.

## Brute-Force Schutz

4  ## Log




