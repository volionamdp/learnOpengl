## 1. Setup shader
Dưới đây là biểu đồ các bước cơ bản để setup shader
được sử dụng để sửa đổi dấu chấm động và định hình, có ba mức highp \ mediump, lowp

![N|Solid](https://raw.githubusercontent.com/volionamdp/learnOpengl/setup/image/setup_shader.jpg)


## 2. Triển khai tạo liên kết shader
- Cơ bản nhất
```java
// type ở đây gồm 2 loại là GLES20.GL_VERTEX_SHADER và GLES20.GL_FRAGMENT_SHADER tương ứng với  vertex shader và fragment shader trong biểu đồ, codeStr là mã code shader hướng dẫn ở bài đầu
private static int loadShader(int type, String codeStr) {
        //Tạo một shader mới theo type và trả về liên kết với shader
        int shader = GLES20.glCreateShader(type);
        if (shader > 0) {
            // Set codeStr vào vào shader đã được tạo
            GLES20.glShaderSource(shader, codeStr);
            //Biên dịch bộ đổ bóng
            GLES20.glCompileShader(shader);
        }
        return shader;
    }
```
- Thêm bước kiểm tra lỗi
```java
private static int loadShader(int type, String codeStr) {
        //Tạo một shader mới theo type và trả về liên kết với shader
        int shader = GLES20.glCreateShader(type);
        if (shader > 0) {
            // Set codeStr vào vào shader đã được tạo
            GLES20.glShaderSource(shader, codeStr);
            //Biên dịch bộ đổ bóng
            GLES20.glCompileShader(shader);

        
            // kiểm tra lỗi
            int[] status = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
            Log.i(TAG, "loadShader: status[0]=" + status[0]);
            if (status[0] == 0) {
                //loại bỏ shader 
                GLES20.glDeleteShader(shader);
                return 0;
            }
        }
        return shader;
    }
```

## 3. Triển khai hoàn chỉnh
```java
public static int loadProgram(String verCode, String fragmentCode) {
        //1. Tạo một chương trình xử lý Shader và trả liên kết chương trình
        int programId = GLES20.glCreateProgram();
        if(programId == 0){
            Log.e(TAG, "loadProgram: glCreateProgram error" );
            return 0;
        }
        //2. Đính kèm shader vào chương trình
        GLES20.glAttachShader(programId, loadShader(GLES20.GL_VERTEX_SHADER, verCode));
        GLES20.glAttachShader(programId, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode));
        //3. Liên kết
        GLES20.glLinkProgram(programId);
        //4. Sử dụng
        GLES20.glUseProgram(programId);
        return programId;
    }
```
