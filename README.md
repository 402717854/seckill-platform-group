 #### 项目讲解
      
      以release版本线为基准
      项目:
        seckill-platform-common 公共组件
        seckill-platform-gateway 网关服务
        seckill-platform-admin 后台系统用户登录权限服务
        seckill-platform-sso 前台用户登录服务
        seckill-platform-auth 全平台鉴权服务
        seckill-platform-service-group 全平台业务服务组
          -goods-service-group 商品服务
          -order-service-group 订单服务
          -seckill-service-group 秒杀活动服务
        seckill-platform-system 后端系统服务
#### 操作手册 (release版本分支详见seckill-platform-system模块的文档)
       master版本分支(实际已放弃)可以参看此操作文档
        准备:
       localhost:8080/resource/initResourceRolesMap redis权限数据预热(否则后端权限校验不通过)
       1、后台登录:(登录之后会将相关token存入到redis中)
        url:localhost:8201/seckill-platform-admin/admin/login
       请求方式:post
       head:
       Content-Type:application/json
       body:
       {
       "username":"admin",
       "password":"macro123"
       }
       2、获取用户信息
       localhost:8201/seckill-platform-admin/admin/loadByUsername?username=admin
        head:
       Authorization:Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdLCJpZCI6MywiZXhwIjoxNjYyNDMxMjI1LCJhdXRob3JpdGllcyI6WyI1X-i2hee6p-euoeeQhuWRmCJdLCJqdGkiOiI4YmJiZDUxZC1iODIzLTQwMTMtYWVjMS1mNDhlM2I4NDgyYmEiLCJjbGllbnRfaWQiOiJhZG1pbi1hcHAifQ.YA5tkH5DJUCZp1JPJeL_wLckgdF0c6Pu0z4M6eUOO3bRhWNFNreqQa3X9Pb4CimZhHfBJGUqX69ALLdp7SC7zkYXcO53E6HAoVXBEq4Y4aEcyTeQyPyKPijXTQkvFWPiLH3xSE_GsBGXVc8fjs39UXM-DyzCLfSPDKqsZTh3Y-k
       3、前台注册
       localhost:8201/seckill-platform-sso/sso/register?username=wys&password=123456&telephone=13312341234&authCode=1234
       请求方式:post
       4、前台登录
       localhost:8201/seckill-platform-sso/sso/login?username=wys&password=123456
       请求方式:post
       5.秒杀
       localhost:8201/seckill-server/seckill/execute
       请求方式:post
       body:
          {
                "userId":"12465",
                "activityId":"123456789"
            }
       head
         Authorization:Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ3eXMiLCJzY29wZSI6WyJhbGwiXSwiaWQiOjEwLCJleHAiOjE2NjI1NTg4OTAsImF1dGhvcml0aWVzIjpbIuWJjeWPsOS8muWRmCJdLCJqdGkiOiI1YTVkNDVkOC03YTVjLTRiYjEtOTgxMy1jMzY2MDFkM2IwNzIiLCJjbGllbnRfaWQiOiJwb3J0YWwtYXBwIn0.Lea6eU-gh3mdl3oLuOzhtPnh2_ozqSjaLtgCOVzHrIevxSOiQobqYKyO3Ub4fm2Q5skJ7LkgDfobIZ9EpC-DHXip8ojaeApKAmjAEUz0MPRnO8_elbCZ9QVQSIzmxZJucZFia77l3A17X8JxXXf340iJEbKtQjg3Fc1I49HsAYc
         Content-Type:application/json