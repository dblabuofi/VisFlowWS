/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.restfulapplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.mycompany.autointerfacews.algorithm.Execution;
import com.mycompany.autointerfacews.algorithm.Topk;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dao.MyHttpClient;
import com.mycompany.autointerfacews.dataIcon.Function;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.generator.OutputGenerator;
import com.mycompany.autointerfacews.helper.BashHelper;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.helper.WebResourceImageDownloader;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyGraph;
import com.mycompany.autointerfacews.smtch.SMatch;
import com.mycompany.autointerfacews.wrapper.WrapperCollections;
import com.mycompany.visflowsmatch.IMatchManager;
import com.mycompany.visflowsmatch.MatchManager;
import com.mycompany.visflowsmatch.classifiers.CNFContextClassifier;
import com.mycompany.visflowsmatch.classifiers.IContextClassifier;
import com.mycompany.visflowsmatch.data.mappings.HashMapping;
import com.mycompany.visflowsmatch.data.mappings.IMappingFactory;
import com.mycompany.visflowsmatch.data.trees.IContext;
import com.mycompany.visflowsmatch.data.trees.INode;
import com.mycompany.visflowsmatch.deciders.CachingSolver;
import com.mycompany.visflowsmatch.deciders.ISATSolver;
import com.mycompany.visflowsmatch.deciders.SAT4J;
import com.mycompany.visflowsmatch.elements.IElementMatcher;
import com.mycompany.visflowsmatch.filters.IMappingFilter;
import com.mycompany.visflowsmatch.filters.SPSMMappingFilter;
import com.mycompany.visflowsmatch.matchers.element.IStringBasedElementLevelSemanticMatcher;
import com.mycompany.visflowsmatch.matchers.element.RunnableElementMatcher;
import com.mycompany.visflowsmatch.matchers.element.string.EditDistanceOptimized;
import com.mycompany.visflowsmatch.matchers.element.string.NGram;
import com.mycompany.visflowsmatch.matchers.element.string.Prefix;
import com.mycompany.visflowsmatch.matchers.element.string.Suffix;
import com.mycompany.visflowsmatch.matchers.element.string.Synonym;
import com.mycompany.visflowsmatch.matchers.structure.node.DefaultNodeMatcher;
import com.mycompany.visflowsmatch.matchers.structure.node.INodeMatcher;
import com.mycompany.visflowsmatch.matchers.structure.node.OptimizedStageNodeMatcher;
import com.mycompany.visflowsmatch.matchers.structure.tree.mini.OptimizedStageTreeMatcher;
import com.mycompany.visflowsmatch.matchers.structure.tree.spsm.SPSMTreeMatcher;
import com.mycompany.visflowsmatch.oracles.ILinguisticOracle;
import com.mycompany.visflowsmatch.oracles.ISenseMatcher;
import com.mycompany.visflowsmatch.oracles.wordnet.WordNet;
import com.mycompany.visflowsmatch.preprocessors.IContextPreprocessor;
import com.mycompany.visflowsmatch.preprocessors.RunnableDefaultContextPreprocessor;
import com.mycompany.visflowsmatch.structure.tree.ITreeMatcher;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author jupiter
 */
public class GuiceBindingConfigureation extends AbstractModule {

    @Override
    public void configure() {
        List<String> arguments = new ArrayList<>();
        arguments.add("taskkill");
        arguments.add("/f");
        arguments.add("/im");
        arguments.add("phantomjs.exe");
        try {
            BashHelper.run("C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\data", arguments);
        } catch (Exception e) {
        }

        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
        ((DesiredCapabilities) caps).setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "C:\\Program Files\\phantomjs-2.0.0\\bin\\phantomjs.exe"
        );
        WebDriver driver = new PhantomJSDriver(caps);
        bind(WebDriver.class).annotatedWith(Names.named("driver")).toInstance(driver);

        Gson gson = new GsonBuilder().create();
        binder().bind(Gson.class).toInstance(gson);

//                MySQLCon mySQLCon = new MySQLCon();
//                binder().bind(MySQLCon.class).toInstance(mySQLCon);
        Topk topk = new Topk();
        binder().bind(Topk.class).toInstance(topk);

        HttpClient client = HttpClientBuilder.create().build();
        binder().bind(HttpClient.class).toInstance(client);

        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        binder().bind(CloseableHttpClient.class).toInstance(closeableHttpClient);

        MyHttpClient myHttpClient = new MyHttpClient();
        binder().bind(MyHttpClient.class).toInstance(myHttpClient);

        EXist eXist = new EXist();
        binder().bind(EXist.class).toInstance(eXist);

        SMatch sMatch = new SMatch();
        binder().bind(SMatch.class).toInstance(sMatch);

        //my classes
        MyGraph myGraph = new MyGraph();
        binder().bind(MyGraph.class).toInstance(myGraph);

        Execution execution = new Execution();
        binder().bind(Execution.class).toInstance(execution);

        BioFlowService bioFlowService = new BioFlowService();
        binder().bind(BioFlowService.class).toInstance(bioFlowService);

        WebResourceImageDownloader webResourceImageDownloader = new WebResourceImageDownloader();
        binder().bind(WebResourceImageDownloader.class).toInstance(webResourceImageDownloader);

        InputGenerator inputGenerator = new InputGenerator(eXist);
        binder().bind(InputGenerator.class).toInstance(inputGenerator);

        OutputGenerator outputGenerator = new OutputGenerator();
        binder().bind(OutputGenerator.class).toInstance(outputGenerator);

        WrapperCollections wrapperCollections = new WrapperCollections();
        binder().bind(WrapperCollections.class).toInstance(wrapperCollections);

        //https
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
//                HostnameVerifier hv = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER; 

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
//                        SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
//                        HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sc,
                    new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "SSLv3"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            CloseableHttpClient httpsClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            binder().bind(CloseableHttpClient.class).annotatedWith(Names.named("httpsClient")).toInstance(httpsClient);

        } catch (Exception e) {

        }

        //VisFlow S-Match
        try {
            String jwnlPropertiesPath = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\AutoInterfaceWS\\src\\main\\resources\\conf\\" + "wn31resource.xml";

            System.out.println(jwnlPropertiesPath);
            ISenseMatcher senseMatcher = new WordNet(jwnlPropertiesPath);
//                        ILinguisticOracle linguisticOracle = (ILinguisticOracle) senseMatcher;
            ILinguisticOracle linguisticOracle = new WordNet(jwnlPropertiesPath);
            IContextPreprocessor contextPreprocessor = new RunnableDefaultContextPreprocessor(senseMatcher, linguisticOracle);//needed
            IContextClassifier contextClassifier = new CNFContextClassifier();//needed

            IMappingFactory mappingFactory = new HashMapping();
            boolean useWeakSemanticsElementLevelMatchersLibrary = true;
            List<IStringBasedElementLevelSemanticMatcher> stringMatchers = new ArrayList<>();
            stringMatchers.add(new Synonym());
            stringMatchers.add(new Prefix());
            stringMatchers.add(new Suffix());
            stringMatchers.add(new NGram());
            stringMatchers.add(new EditDistanceOptimized());

            IElementMatcher elementMatcher = new RunnableElementMatcher(mappingFactory, senseMatcher,
                    useWeakSemanticsElementLevelMatchersLibrary,
                    stringMatchers,
                    null);//needed

            ISATSolver sat4J = new SAT4J();
            ISATSolver satSolver = new CachingSolver(sat4J);
            OptimizedStageNodeMatcher nodeMatcher = new OptimizedStageNodeMatcher(satSolver);

            ITreeMatcher treeMatcher = new OptimizedStageTreeMatcher(mappingFactory, nodeMatcher);//needed
            //M
            IMatchManager mm = new MatchManager(contextPreprocessor, contextClassifier, elementMatcher, treeMatcher);

            binder().bind(IMatchManager.class).toInstance(mm);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        executorService.setMaximumPoolSize(4);
        binder().bind(ThreadPoolExecutor.class).toInstance(executorService);

        ScheduledThreadPoolExecutor scheduledExecutorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
        scheduledExecutorService.setMaximumPoolSize(2);
        binder().bind(ScheduledThreadPoolExecutor.class).toInstance(scheduledExecutorService);

        String hostname = "Unknown";
        InetAddress addr;
        String mysqlURL = "";
        try {
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            mysqlURL = hostname.contains("CS-PREC3620HJ") ? "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql"
                    : "C:\\Program Files (x86)\\MySQL\\MySQL Server 5.7\\bin\\mysql";
        } catch (Exception e) {
            e.printStackTrace();
        }
        bind(String.class).annotatedWith(Names.named("mysqlURL")).toInstance(mysqlURL);

    }
}
