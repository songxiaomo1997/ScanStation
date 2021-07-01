# ScanStation
一个可以自定规则的主动扫描器

#### 目前支持
- [x] 自定义规则
- [x] 全局参数设置 请求头,参数,cookie
- [x] 多线程扫描
- [x] 多目标扫描
- [x] POST,GET类型扫描
- [x] json,Multi,form类型请求扫描
- [x] 时间延迟判断,响应内容判断
#### 后续支持
- [ ] 请求方式PUT,DELETE等
- [ ] 支持多语言poc,如python
- [ ] 请求类型支持xml类型
- [ ] payload表达式增强,支持payload组，自定payload变量
- [ ] 支持被动扫描
- [ ] 支持反连平台带外检测
- [ ] 支持更多表达式检测结果
- [ ] 支持grpc扫描
- [ ] 分布式部署

### 使用方法
```
java -jar ScanStation -u|--url [目标地址] --pocPath [需要加载的poc路径] -c|--cookie [(可选)全局cookie] -gP|--globalParam[(可选)全局参数] -hC|--headerConfig [(可选)全局请求头配置文件路径] -t 
```
####参数说明
```
-u | --url [目标地址] 如htts://127.0.0.1:1234
--target [目标存放文本位置] 从txt文本获取需要扫描的url进行扫描 
-p | --pocPath [需要加载的poc路径] 需要加载的poc的路径,需要在目录下放需要加载规则 如:~/Dowloads/rules
-c|--cookie (可选)全局cookie 设置全局的cookie 将请求中的cookie值复制即可
-gP|--globalParam [(可选)全局参数] 设置全局参数 在请求时会自动带上该参数进行请求
-hC|--headerConfig [(可选)全局请求头配置文件路径] 配置文件为yaml类型格式为: test : 12321321 
-t | --threads [(可选) 线程数默认10个线程] 设置线程数
```
#### 规则测试
只会加载一个poc用于测试poc路径为文件路径,如poc路径为~/Dowloads/rules/test.yaml则填入即可
```
java -jar ScanStation -u [目标地址] -p [需要加载的poc路径] -debug
```

### poc说明
POC采用yaml加载后续支持更多类型加载
```yaml
name: Form-Post #poc名称
rules:
  method: POST #支持方法GET|POST
  path: /vul/sql/POST #漏洞路径 
  originalParam: id=123&name=songxiaomo #原始请求参数
  vulParam: id&name #需要扫描的漏洞参数如:id=1&name=songxiaomo则会对这两个参数进行扫描，可没有原始值
  header:  #自定义请求头键值对方式,如需要对请求头扫描则设置headerscan值为true 可不填
    test-Post: test-Post123
    tmp : tmp12321313213213
  headerscan : true #true | false 如果为true则会对请求头替换参数扫描
  payloads:
    - payload: "and sleep(1)" #payload为String类型请用""包裹
      expression: string.contains(body,'Program Files') #表达式判断响应中结果与预期结果是否相符
  expressions: true #默认值为true则输出payloads中判断成功所以结果。id=1和第一个payload组合为payload0 若存在多个参数如：id=1&name=songxiaomo 和两个payload 则id=1与第一个paylaod和为payload0与第二个为payload1以此类推
  type: Form #poc的请求方式目前支持Form和json 后续支持|Multi|xml|path
detail: 漏洞详情
  author: songxiaomo #作者
  links: #相关连接传入数组
    - https://www.google.com
```
#### 重点参数说明
##### payload:
一个payload是由一个payload和对应表达式组成，可以填入多个payload进行扫描并在expressions中做整体判断。
以下为两个payload一个是sleep(1)对应就可以用sleep()判断是否存在时间沉睡，如果存在就返回true。
```yaml
payloads:
    - payload: "and sleep(1)"
      expression: sleep()
    - payload: "and exp(710)"
      expression: string.contains(body,'error') 
expressions: true
```
在这个地方存在两个payload，如果此时vulParam值为id&name且header值为空则会产生4个payload并从payload0开始编号
```
payload0 id=and sleep(1) sleep()
payload1 id=and exp(710) string.contains(body,'error') 
payload2 name=and sleep(1) sleep()
payload3 name=and exp(710) string.contains(body,'error') 
```
此时如果expressions值为(pyload0&&payload1) || (payload2&&payload3)则payload0和1都达到预期效果或者payload2和3都达到预期效果才会确定漏洞存在。
PS：如果headerscan为true则会像参数一样构造payload区别是header永远在参数构造完后开始构造
##### expression:
###### sleep()
通过发送30次正常请求获取响应时间取响应时间平均差,如果带payload请求在平均差范围外则存在时间沉睡
###### string.contains()
需要两个参数第一个为判断的位置支持:body,header,status,time。
第二个参数为存在的字符串
```
string.contains(body,"error") #如果响应体中存咋error则返回true
```
### 规则示范
#### Form-Post
```yaml
name: Form-Post
rules:
  method: POST #GET|POST
  path: /vul/sql/POST #漏洞路径 /api/geturl/127.0.0.1*
  originalParam: id=123&name=songxiaomo
  vulParam: id=1&name #需要扫描的漏洞参数如:id=1&name=songxiaomo /api/geturl/*/*
  header:
    test-Post: test-Post123
    tmp : tmp12321313213213
  headerscan : true #true | false
  payloads:
    - payload: "and sleep(1)"
      expression: sleep(1) 
  expressions: true 
  type: Form #form(OK)|Multi|json|path 后期加入
detail:
  author: songxiaomo
  links:
    - https://www.google.com
```
#### Json-Post
```yaml
name: Json-Post
rules:
  method: POST #GET|POST
  path: /vul/sql/Json 
  originalParam: "{\"code\":200,\"msg\":\"OK\",\"muser\":[{\"name\":\"zhangsan\",\"age\":\"10\",\"phone\":\"11111\",\"email\":\"11111@11.com\"},{\"name\":\"lisi\",\"age\":\"20\",\"phone\":\"22222\",\"email\":\"22222@22.com\"}]}"
  vulParam: msg&name
  header:
    test-Post: test-Json123
    tmp : tmp12321313213213
  headerscan : true 
  payloads:
    - payload: "and sleep(1)"
      expression: sleep()
  expressions: false 
  type: Json 
detail:
  author: songxiaomo
  links:
    - https://www.google.com
```
#### Form-GET
```yaml
name: Form-GET
rules:
  method: GET 
  path: /vul/sql/GET 
  originalParam: id=123&name=songxiaomo
  vulParam: id=1&name 
  header:
    test-Post: test-Post123
    tmp : tmp12321313213213
  headerscan : false 
  payloads:
    - payload: "and sleep(1)"
      expression: sleep(1)
  expressions: true 
  type: Form 
detail:
  author: songxiaomo
  links:
    - https://www.google.com
```