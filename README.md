# springboot-multi-datasource
springboot 实现mysql数据库主从复制，读写分离
1、选择两台网络互通，安装相同版本mysql的服务器，例如192.168.28.108,192.168.29.54(这两台是测试服务器同一个ova导出来的)

2、选择其中一台作为master，修改/etc/my.conf文件
server-id= 1 #设置server_id
log-bin= /var/log/mysql/mysql-bin.log  #开启二进制日志功能，可以随便取，最好有含义
binlog-do-db=test  #复制过滤：需要备份的数据库，输出binlog
binlog-ignore-db=mysql  #复制过滤：不需要备份的数据库，不输出（mysql库一般不同步）
binlog_cache_size=1M  #为每个session 分配的内存，在事务过程中用来存储二进制日志的缓存
binlog_format=mixed #主从复制的格式（mixed,statement,row，默认格式是statement）
expire_logs_days=7 #二进制日志自动删除/过期的天数。默认值为0，表示不自动删除。
slave_skip_errors=1062 #跳过主从复制中遇到的所有错误或指定类型的错误，避免slave端复制中断。如：1062错误是指一些主键重复，1032错误是因为主从数据库数据不一致

 

修改完my.cnf 运行 service mysql restart 重启mysql。
3、运行mysql -uroot –p 进入mysql 然后配置完成后需要建立同步用的数据库账户
进入Master Mysql终端命令（test为同步数据库，@后面是从数据库服务器，identified by后面是数据库密码）：
Grant replication slave on "." to 'test'@'192.168.29.54' identified by 's                                                                                                                                                             ystec';

运行FLUSH PRIVILEGES;刷新设置
 
 

4、运行show master status;可以查看主库状态，如果看到mysql-bin.000001 这一行，说明master设置成功了
 

5、然后cd /var/lib/mysql 进入刚才配置的log-bin目录，运行 ll查看文件，如果看到

 

6、选择192.168.29.54作为slave，同样需要修改 /etc/my.cnf文件（注意在设置slave时，如果master数据库上有数据的，要把数据导出来，导入到slave数据库上，并关闭数据库插入更新操作流，保证master与slave之间数据一致性）
server-id=2 # server-id不能跟master主库一样，slave之间也不能设置成一样
log-bin=/var/lib/mysql/mysql-bin.log  # 开启二进制日志
 
7、因为我们的服务器是同一个ova导出来的，所以 auto.cnf文件里面的server-uuid是一样的，这个要改成不一样，我们服务器上放在 /opt/data/auto.cnf

 
 
设置完就可以运行 service mysql restart重启mysql 了

8、运行 mysql –uroot –p 进入mysql ，做slave设置

运行
Change master to 
maste_host=’192.168.28.108’,        //master服务器
master_user='root',                //master数据库用户
master_password='systec' ,          //master数据库密码
master_log_file='mysql-bin.000002',  //在master 服务器 show master status 看到的文件
master_log_pos=393549;

 

 

9、start slave 开启slave

10、运行show slave status\G;查看slave状态，当看到 slave_io_running 与slave_sql_running都为yes的时候，就说明mysql主从复制功能已经实现了，就可以在master上面走修改插入操作，在slave上验证是否数据有复制过来的。


 
