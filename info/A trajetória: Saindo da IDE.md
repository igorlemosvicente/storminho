# Dia 12 de dezembro de 2016
## Começando o cluster do Storm
Seguindo o [Setting up a Storm Cluster](http://storm.apache.org/releases/1.0.1/Setting-up-a-Storm-cluster.html)
### Instalando Zookeeper
Seguindo o [ZooKeeper Getting Started Guide](zookeeper.apache.org/doc/r3.3.3/zookeeperStarted.html#sc_InstallingSingleMode)  
1. Entrei em [ZooKeeper Releases](http://ftp.unicamp.br/pub/apache/zookeeper/)  
1. Baixei o arquivo `zookeeper-3.4.9.tar.gz`
1. Extrai a pasta `zookeeper-3.4.9` para a área de trabalho e a renomeei para `Zookeeper`  
1. Abri o arquivo `conf/zoo.cfg` e colei:
```
tickTime=2000
dataDir=/var/zookeeper
clientPort=2181
```
1. Iniciei com o comando `sudo bin/zkServer.sh start`

### Parte do Storm
1. Acessei [Storm downloads](http://storm.apache.org/downloads.html) e baixei o arquivo `apache-storm-1.0.2.tar.gz`  
1. Extrai a pasta `apache-storm-1.0.2` para a área de trabalha e a renomeei para `Storm`  
1. Abri o arquivo `conf/storm.yaml` com um editor de texto e inseri o seguinte pedaço de texto:
```
storm.zookeeper.servers:
 - "localhost"
storm.local.dir: "/mnt/storm"
nimbus.seeds: ["localhost"]
supervisor.slots.ports:
    - 6700
    - 6701
    - 6702
    - 6703
```
1. Por fim, deixo este pedaço de texto que tem no tutorial. Não rodei nada disso ainda:
>**Nimbus**: Run the command `bin/storm nimbus` under supervision on the master machine.  
>**Supervisor**: Run the command `bin/storm supervisor` under supervision on each worker machine. The supervisor daemon is responsible for starting and stopping worker processes on that machine.  
>**UI**: Run the Storm UI (a site you can access from the browser that gives diagnostics on the cluster and topologies) by running the command `bin/storm ui` under supervision. The UI can be accessed by navigating your web browser to `http://{ui_host}:8080`  

### Para rodar na máquina local
Seguindo o [Running Topologies on a Production Cluster](http://storm.apache.org/releases/0.10.2/Running-topologies-on-a-production-cluster.html)  
Primeiro coisa que fiz foi, no Netbeans, procurar a
> ###### Dependência 1 `org.apache.maven.plugins : maven-assembly-plugins`  

e adicioná-la ao projeto (`3.0.0 [jar] central`).  
Depois:  
1. Dei um `Build` no projeto no Netbeans
1. Executei os comandos do item 4 da [Parte do Storm](#parte-do-storm), (Exceto o referente à **UI**) como super usuário pelo terminal e deixei em segundo plano  
1. Ainda na pasta `Storm`, dei o seguinte comando
> ###### Comando 1 `sudo bin/storm jar $STORMINHO/target/storminho-1.0.jar edu.uffs.storminho.topologies.MainTopology`
1. Deu o seguinte erro:  
    > ###### Erro 1 `Exception in thread "main" java.lang.NoClassDefFoundError: redis/clients/jedis/Jedis`  

    Eu já tinha suspeitas que poderia dar esse erro. O problema é que no Build, ele não está incluindo as dependências. Eu tenho que dar um `Build with Dependencies` no Netbeans e usar o `.jar` resultante disso. Porém, no tutorial colocado no começo desse capítulo, tem uma linha que diz:
    >Then run mvn assembly:assembly to get an appropriately packaged jar. Make sure you [exclude](http://maven.apache.org/plugins/maven-assembly-plugin/examples/single/including-and-excluding-artifacts.html) the Storm jars since the cluster already has Storm on the classpath.  

    Ou seja, eu acho que preciso criar um `.jar` com todas as dependências necessários, exceto a do Storm.  
1. Mudei o escopo da dependência `org.apache.storm : storm-core` para `provided` indo em `Adicionar dependência` no Netbeans, procurando esta mesma versão e, na hora de adicionar, mudando o escopo para `provided`
1. Dei um `Build with Dependencies` e executei o [Comando 1](#comando-1). Persistiu o [Erro 1](#erro-1). Fiz o mesmo trocando `Build with Dependencies` por `Build` e também obtive [o erro](#erro-1)
1. Mudei o escopo do `org.apache.storm : storm-core` para o padrão novamente excluindo a linha `<scope>provided</scope>` em
    ```
<dependency>
    <groupId>org.apache.storm</groupId>
    <artifactId>storm-core</artifactId>
    <version>1.0.2</version>
    <scope>provided</scope>
</dependency>
```
    no arquivo `pom.xml` do projeto
1. Comecei a pesquisar o motivo de estar acontecendo o [Erro 1](#erro-1)
