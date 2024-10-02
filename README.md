### **Bước 1: Cấu hình master**

1. **Cập nhật hệ thống:**
   ```bash
   sudo apt update
   ```

2. **Tạo user cho Hadoop:**
   ```bash
   adduser hadoop
   sudo usermod -aG sudo hadoop
   ```

3. **Chuyển sang user vừa tạo:**
   ```bash
   su hadoop
   ```

4. **Cài đặt Java JDK:**
   ```bash
   sudo apt install openjdk-8-jdk -y
   ```

5. **Kiểm tra phiên bản Java:**
   ```bash
   java -version; javac -version
   ```

6. **Cài đặt SSH:**
   ```bash
   sudo apt install ssh
   ```

7. **Tạo key SSH cho master:**
   ```bash
   ssh-keygen -t rsa -P ""
   cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
   chmod 0600 ~/.ssh/authorized_keys
   ```

8. **Tải và cài đặt Hadoop:**
   ```bash
   wget https://dlcdn.apache.org/hadoop/common/hadoop-3.4.0/hadoop-3.4.0.tar.gz
   tar xzf hadoop-3.4.0.tar.gz
   sudo mv hadoop-3.4.0 ~/hadoop
   ```

9. **Lấy đường dẫn JDK:**
   ```bash
   readlink -f /usr/bin/javac 
   # Hoặc
   dirname $(dirname $(readlink -f $(which java)))
   ```

10. **Cấu hình biến môi trường trong `.bashrc`:**
    ```bash
    nano ~/.bashrc
    ```
    Thêm các dòng sau vào cuối file:
    ```bash
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
    export HADOOP_HOME=/home/hadoop/hadoop
    export HADOOP_INSTALL=$HADOOP_HOME
    export HADOOP_MAPRED_HOME=$HADOOP_HOME
    export HADOOP_COMMON_HOME=$HADOOP_HOME
    export HADOOP_HDFS_HOME=$HADOOP_HOME
    export HADOOP_YARN_HOME=$HADOOP_HOME
    export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
    export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin
    export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native"
    ```

11. **Áp dụng thay đổi:**
    ```bash
    source ~/.bashrc
    ```

12. **Cấu hình Hadoop:**

    - **core-site.xml:**
      ```bash
      nano $HADOOP_HOME/etc/hadoop/core-site.xml
      ```
      Thêm nội dung:
      ```xml
      <configuration>
          <property>
              <name>fs.defaultFS</name>
              <value>hdfs://masternode:9000</value>
          </property>
      </configuration>
      ```

    - **hdfs-site.xml:**
      ```bash
      nano $HADOOP_HOME/etc/hadoop/hdfs-site.xml
      ```
      Thêm nội dung:
      ```xml
      <configuration>
          <property>
              <name>dfs.replication</name>
              <value>1</value>
          </property>
          <property>
              <name>dfs.name.dir</name>
              <value>file:///home/hadoop/hadoopdata/hdfs/namenode</value>
          </property>
          <property>
              <name>dfs.data.dir</name>
              <value>file:///home/hadoop/hadoopdata/hdfs/datanode</value>
          </property>
      </configuration>
      ```

13. **Tạo thư mục cho Namenode và Datanode:**

    - Nếu bạn sử dụng đường dẫn trong home directory:
      ```bash
      mkdir -p ~/hadoopdata/hdfs/namenode
      mkdir -p ~/hadoopdata/hdfs/datanode
      sudo chown hadoop:hadoop -R ~/hadoopdata/hdfs/datanode
      chmod 700 ~/hadoopdata/hdfs/datanode
      sudo chown hadoop:hadoop -R ~/hadoopdata/hdfs/namenode
      chmod 700 ~/hadoopdata/hdfs/namenode
      ```

    - Hoặc sử dụng đường dẫn `/usr/local`:
      ```bash
      sudo mkdir -p /usr/local/hadoop/hdfs/data
      sudo chown hadoop:hadoop -R /usr/local/hadoop/hdfs/data
      chmod 700 /usr/local/hadoop/hdfs/data
      ```

14. **Cấu hình danh sách worker (nếu có slave):**
    ```bash
    nano $HADOOP_HOME/etc/hadoop/workers
    ```
    Thêm vào file:
    ```
    salve
    ```

---

### **Bước 2: Cấu hình slave**

1. **Sao chép cấu hình từ master sang slave.**

2. **Đổi hostname của slave:**
   ```bash
   hostnamectl set-hostname salve
   ```

3. **Cập nhật file `/etc/hosts`:**
   ```bash
   nano /etc/hosts
   ```
   Thêm thông tin IP của master và slave:
   ```
   111.111.1.1 hadoop
   111.111.2.2 salve
   ```

4. **Tạo SSH key trên slave và kết nối với master:**
   ```bash
   ssh-keygen -t rsa -P ""
   cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
   chmod 0600 ~/.ssh/authorized_keys
   ssh-copy-id hadoop@salve
   ```

5. **Sao chép cấu hình Hadoop từ master sang slave:**
   ```bash
   scp $HADOOP_HOME/etc/hadoop/* hadoop@salve:$HADOOP_HOME/etc/hadoop/
   ```

---

### **Bước 3: Khởi động Hadoop**

1. **Format namenode:**
   ```bash
   hdfs namenode -format
   ```

2. **Khởi động DFS:**
   ```bash
   start-dfs.sh
   ```

3. **Kiểm tra tiến trình với `jps`:**
   ```bash
   jps
   ```

4. **Cấu hình và khởi động YARN:**

    - **yarn-site.xml:**
      ```bash
      nano $HADOOP_HOME/etc/hadoop/yarn-site.xml
      ```
      Thêm nội dung:
      ```xml
      <property>
          <name>yarn.resourcemanager.hostname</name>
          <value>hadoop</value>
      </property>
      ```

5. **Khởi động YARN:**
   ```bash
   start-yarn.sh
   ```
