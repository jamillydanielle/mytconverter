# mytconverter

Projeto integrador para conversão de arquivos com suporte a diferentes formatos de mídia.

---

## 📚 Diagrama de classes

```mermaid
classDiagram
    class User {
        -int id
        -string username
        -string password
        +get_username()
        +User()
    }

    class Conversion {
        -string url
        -double file_size
        -int user_id
        +get_url()
        +Conversion()
    }

    class MP3 {
        -int bit_rate
    }

    class MP4 {
        -int video_resolution
    }

    User "1" o-- "many" Conversion : has
    Conversion <|-- MP3
    Conversion <|-- MP4
