### 问题总结
    一、打包找不到执行主类问题
           repackage:maven执行完package之后再次进行打包，会将执行主类放入jar包中
           执行jar包system-service-1.0-SNAPSHOT.jar
           原始jar包system-service-1.0-SNAPSHOT.jar.original
           <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring.boot.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
    二、关于日志配置文件加载日志相关error
            logback.xml和logback-spring.xml都可以用来配置logback，但是两者的加载顺序是不一样的，具体顺序如下：
            logback.xml->application.properties->logback-spring.xml
            从上边的加载顺序可以看出，logback.xml加载早于application.properties，
            所以如果你在logback.xml使用了变量时，而恰好这个变量是写在application.properties中，
            那么就会获取不到，报上边的错误信息。
            no applicable action for [springProfile], current ElementPath is [[configuration][springProfile]]
    三、多环境配置问题
        <!--执行命令#构建正式包
                maven package -P prod -Dmaven.test.skip=true-->
        <profiles>
            <profile>
                <id>dev</id>
                <activation>
                    <!--默认激活配置,maven打包默认选用的配置-->
                    <activeByDefault>true</activeByDefault>
                </activation>
                <properties>
                    <!--当前环境自定义配置,标签名自定义-->
                    <profile.name>dev</profile.name>
                </properties>
            </profile>
            <profile>
                <id>test</id>
                <properties>
                    <profile.name>test</profile.name>
                </properties>
            </profile>
            <profile>
                <id>prod</id>
                <properties>
                    <profile.name>prod</profile.name>
                </properties>
            </profile>
        </profiles>
    
        <build>
            <resources>
                <resource>
                    <!-- 指定资源文件夹，src/main/resources 默认打到classes下-->
                    <directory>src/main/resources</directory>
                    <!-- 默认为false，配置为true，则会将改资源目录下的xml和properties文件中的引用 @配置@  和 ${} 转换成真实值-->
                    <filtering>true</filtering>
                    <!-- 指定要打包的文件或目录（只包含资源源文件，不包括class -->
                <!--                <includes>-->
                <!--                    <include></include>-->
                <!--                </includes>-->
                    <!-- 指定要过滤的文件或目录 （只包含资源源文件，不包括class-->
                <!--                <excludes>-->
                <!--                    <exclude></exclude>-->
                <!--                </excludes>-->
                </resource>
            </resources>
        </build>
