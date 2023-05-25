
# Move File MFTP Microservice [ms.movfile_mftp]  
El propósito de este microservicio consiste en cargar archivos en el servidor MFTP.


## Cómo funciona  
1. **ms.movfile_mftp** recibe el mensaje de un *cliente http* indicando la carga de un archivo  
2. Verifica que el archivo exista en el host local  
3. Transfiere una copia del archivo al servidor MFTP  
4. Elimina el original en el host local  
4. Notifica la operación realizada a los servicios **ms.logevent** y **ms.notification**  


## Descripción de la configuración  
Datos relevantes contenidos en los archivos y clases de configuración del proyecto:  

#### service.properties  
+ Información básica de **ms.movfile_mftp**  
    - Versión  
    - Ruta de contexto del servicio  
    - Puerto asignado  
+ URLs de servicios requeridos: **ms.logevent** y **ms.notification**  

#### properties.xml  
+ Datos de conexión al servidor MFTP (host, puerto, usuario y password)  
+ Directorio local de los archivos que serán enviados (propiedad 'source')  
+ Directorio remoto en donde se recibirán los archivos (propiedad 'outbound')  

#### validation.properties  
+ Mensajes de validación que retorna el servicio cuando recibe peticiones con datos inválidos  

#### Application.java  
+ Configuración de zona horaria  


## Descripción de la implementación  
Descripción de clases principales:  

#### Message.java  
+ Clase de dominio para mensajes recibidos de los clientes del servicio
+ Incluye anotaciones para validación básica de datos

#### MftpConfig.java  
+ Carga configuración que utilizará el cliente MFTP   

#### MftpClient.java  
+ Realiza operaciones de bajo nivel para interactuar con el servidor MFTP  

#### MovFileService.java  
+ Verifica estatus y nombre de archivo recibidos en el mensaje  

#### MftpService.java  
Contiene la lógica de negocio de la aplicación:  
+ Con conexión fallida a servidor MFTP, envía evento a **ms.logevent** y **ms.notification**  
+ Si no encuentra archivo local notifica a **ms.logevent**  
+ Si no puede cargar archivo en destino remoto, envía evento a **ms.logevent** y **ms.notification**  
+ Si la carga es exitosa, elimina archivo original en host local y envía evento a **ms.logevent** y **ms.notification**  


## Ejecución de pruebas unitarias  
  `$ mvn test`


## Ejecución de la aplicación  

+ Generación de WAR  
  `$ mvn clean package`  

+ Ejecución de JAR  
  `$ mvn spring-boot:run`  


## Uso  

### Carga correcta de archivo  

#### Request  
    curl -i -X POST -H 'Content-Type: application/json' -d '{"status": 1, "msg": "SUCCESS", "file": "file.txt"}' http://{server-ip}:8082/olympus/movfile_mftp/v1/message  

#### Response  
    HTTP/1.1 200
    Content-Type: text/plain;charset=UTF-8
    Content-Length: 2
    Date: Thu, 25 May 2023 03:43:54 GMT

    OK  

### Carga de archivo con nombre vacío  

#### Request  
    curl -i -X POST -H 'Content-Type: application/json' -d '{"status": 1, "msg": "SUCCESS", "file": ""}' http://{server-ip}:8082/olympus/movfile_mftp/v1/message  

#### Response  
    HTTP/1.1 400
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Thu, 25 May 2023 02:27:13 GMT
    Connection: close

    {"timestamp": "2023-05-24T21:27:13.809-05:00", "status": 400, "error": "Bad Request", "message": "NO se recibió el nombre del archivo", "path": "/olympus/movfile_mftp/v1/message"}  


## Respuestas HTTP / Causa  

#### 200 OK  
+ El archivo solicitado se cargó correctamente  

#### 400 Bad Request  
+ Atributo **{message.status}** nulo  
+ Atributo **{message.msg}** nulo o vacío  
+ El valor del atributo **{message.status}** no es 1 (SUCCESS)  
+ El valor del atributo **{message.status}** es 1 (SUCCESS), pero **{message.file}** es nulo o vacío  

#### 500 Internal Server Error  
+ Sin conexión a servidor MFTP  
+ No se encuentra archivo solicitado en host local  
+ No existe directorio para carga de archivos en servidor MFTP  
+ Carga fallida de archivo  
