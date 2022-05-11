# 
## 1. Kích thước lưu trữ dữ liệu
Dưới đây là mô tả về kích thước lưu chữ
được sử dụng để sửa đổi dấu chấm động và định hình, có ba mức highp \ mediump, lowp

![N|Solid](https://raw.githubusercontent.com/volionamdp/learnOpengl/master/image/640.jpg)


## 2. Các loại dữ liệu cơ bản

Trong đồ họa máy tính, vectơ và ma trận là cơ sở của phép biến đổi, và hai kiểu dữ liệu này cũng là trung tâm của GLSL

vec2, vec3, vec4: vectơ dấu phẩy động (float) : vec2 tương ứng với vector 2 chiều,vec3 là vector 3 chiều .. vd: vec2(2.,1.) ,vec3(1.,2.,3.)
ivec2, ivec3, ivec4: vectơ số nguyên (int)
uvec2, uvec3, uvec4: vectơ số nguyên dương
bvec2, bvec3, bvec4: vectơ boolean (bool)
mat2, mat3, mat4, mat2x3 ...: ma trận dấu chấm động

sampler2D: kết cấu 2d (để dẽ hiểu có thể coi là ảnh 2d)

và các dữ liệu lập trình cơ bản (float,int,...)

## 3. Bổ ngữ (chưa biết gọi là gì)
(từ đang trước kiểu dữ liệu vd:trong java static,const ...)

const: (chỉ đọc) thuộc tính biến hằng số
attribute: chỉ có thể được sử dụng trong bộ đổ bóng đỉnh, cho thông tin thường xuyên thay đổi
uniform: (Nhất quán) đối với thông tin thay đổi không thường xuyên, có thể được sử dụng trong các trình tạo bóng đỉnh và phân mảnh (cái này có thể set liên tục trong code c,java,kotlin... được dùng chính để thay đổi giá trị của shader)
varying: (biến) được sử dụng để sửa đổi biến được truyền từ bộ đổ bóng đỉnh(Vertex Shader) sang bộ đổ bóng phân đoạn(Fragment Shader).

## 3. Biến tích hợp (biến mặc định trong shader)
Phổ biến nhất là các biến đầu ra của bộ đổ bóng đỉnh và bộ tạo bóng phân đoạn

Các biến tích hợp của Vertex Shader: gl_position và gl_pointSize (trả về tọa độ điểm và kích thước điểm)
Fragment Các biến tích hợp của Vertex Shader: gl_FragColor (trả về màu sắc (r,g,b,a) sau sử lí )

## 4. Các hàm,chức năng có sẵn hay dùng
in: chế độ mặc định, cách truyền giá trị, không thể sửa đổi
inout: truyền theo tham chiếu, được phép sửa đổi, sau khi sửa đổi, hàm sẽ thay đổi sau khi thoát
out:  sẽ được sửa đổi khi hàm trả về

vd: 
```c
void test(in float a,out float b){
    b = a*2.;
}
```
    
abs: giá trị tuyệt đối

floor: làm tròn xuống

ceil: làm tròn lên

mod: modulo  (trong này không dùng được kiêu a%b nên phải dùng hàm này)

min: tối thiểu

max: tối đa

clamp:clamp(x,minVal,maxVal) tương đương  min(max(x, minVal), maxVal).

pow: tính lũy thừa

mix: mix(x,y,a) =  x×(1−a)+y×a .
