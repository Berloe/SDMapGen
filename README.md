# Scalable Dto Mapper Generator
## What is SDMapGen? 
  It is a code generator for mapping between Data Transfer Object  from an approach of simplicity of use,  that is the reason for being integrated in Eclipse as a plugin.
## Why SDMapGen? 
  Because it generates while you are coding so you can review the mapping and adjust it while coding, directly in Java and into the classes where it should be.
  The coding of these mappings is tedious and frequent source of errors, by automating it we can avoid both problems and allow us to focus on the most complex and important parts.
  A common problem with mappers is that they require configuration files as the basis for mapping between classes, so that we move the site problem from Java to configuration file without solving it completely.
  SDMapGen is based on a minimal configuration, accessing a class repository to resolve dependencies, the source class and the target class. Of course, the result code should be reviewed and, when appropriate, completed.
## It Is complete? 
  No. This plugin contains two interfaces, one to define dependency resolution as easily as possible and another to incorporate new mappings. By an xml configuration file we can change the resolution of dependencies  or  we can incorporate,  even change, the classes of mapping. This makes this plugin easily scalable and adaptable

## ¿Qué es SDMapGen?
  Es un generador de código para el mapeo entre Data Transfer Object desde enfoque de simplicidad de uso, esa es la razón de estar integrado en Eclipse a modo de plugin.
## ¿Por qué SDMapGen?
  Porque genera mientras que se está codificando de forma que se puede repasar el mapeo y ajustarlo mientras que se codifica, directamente en Java y en las clases donde debe estar.
  La codificación de estos mapeos es tediosa y una frecuente fuente de errores, al automatizarla evitamos ambos problemas y nos permite enfocarnos en las partes más complejas e importantes.
  Un problema común con los mapeadores es que requieren archivos de configuración como base para el mapeo entre clases, de modo que movemos el problema del sitio, desde Java a un archivo de configuración sin resolverlo completamente.
  SDMapGen se basa en una configuración mínima, accediendo a un repositorio de clases para resolver las dependencias, la clase de origen y la clase de destino. Por supuesto, el código de resultado debe ser revisado y, cuando sea apropiado, completado.
## ¿Está completo?
  No. Este plugin contiene dos interfaces, una para definir la resolución de dependencia lo más fácilmente posible y otra para incorporar nuevas asignaciones. Medianteun archivo de configuración xml podemos cambiar la resolución de dependencias o podemos incorporar, incluso cambiar, las clases de mapeo. Esto hace que este plugin sea fácilmente escalable y adaptable
