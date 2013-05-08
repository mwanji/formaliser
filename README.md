# Formaliser

Formaliser uses types and standard annotations to read and write HTML forms with Java objects. It is inspired by Ruby on Rails's form builders.

By default, Formaliser produces minimal markup and is HTML5-aware. Many aspects of the markup can be customised.

## Installation

Requires Java 6 and Maven (unless you are willing to hunt down dependencies yourself).

1. git clone this repo
1. mvn install
1. Add the following to your POM:
```xml
<dependency>
  <groupId>co.mewf.formaliser</groupId>
  <artifactId>formaliser</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

```java
FormWriter formWriter = new FormWriter(MyEntity.class);

formWriter.writeToString("name"); // Returns <div><label>Name</label><input type="text" name="myEntity.name" /></div>
String formHtml = formWriter.writeToString(); // Returns HTML similar to the above for each field in MyEntity

FormReader<MyEntity> formReader = new FormReader<MyEntity>(MyEntity.class);
MyEntity myEntity = formReader.read(params);
```

### Configuration

FormWriter.Config is used to customise various aspects of how forms are written and read.

````java
new FormWriter(MyClass.class, new FormWriter.Config()
  .writer(new FileWriter("myFile.html"))
  .rowWriter(new MyCustomRowWriter()))
  .i18n(new MyFormI18n())
  .inputTypes(new MyInputTypes());
````

* __writer:__ The Writer output is directed to. Defaults to null, meaning the output is only available via FormWriter#writeToString. Common values might be a Servlet's or templating engine's output stream.
* __rowWriter:__ Handles generating the HTML for a single field. Defaults to TemplateRowWriter.
* __i18n:__ Produces internationlised labels. Defaults to FieldNameFormI18n.
* __inputTypes:__ Determines the type of input type to use based on Java types. Defaults to Html5InputTypes.

## TODO

* Maven plugin that generates field name info
* Validation group support
* JPA support

## License

Formaliser in copyright of Moandji Ezana 2013.

Formaliser is licensed under the MIT License.
