package test.crypto;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.testng.annotations.Test;
import xyz.sunqian.common.encode.JieBase64;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CryptoFileTest {

    @Test
    public void testCipher() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // 使用2048位的密钥长度
        KeyPair caPair = keyGen.generateKeyPair();
        KeyPair certPair = keyGen.generateKeyPair();
        X500Name caName = new X500Name("CN=Jie CA, O=Jie CA Organization, C=CN");
        X500Name certName = new X500Name("CN=Jie Cert, O=Jie Cert Organization, C=CN");
        X509Certificate ca = createCaFile(caPair, caName);
        X509Certificate cert = createCertFile(ca, caName, certName, caPair, certPair, 365);
        printCert(ca, "ca");
        printCert(cert, "cert");

        PemObject pemObject = new PemObject("PRIVATE KEY", caPair.getPrivate().getEncoded());
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.flush();
        System.out.println(stringWriter);
        System.out.println(Hex.encodeHexString(caPair.getPrivate().getEncoded()));
        System.out.println(JieBase64.pemEncoder().toLatin(caPair.getPrivate().getEncoded()));
    }

    private X509Certificate createCaFile(KeyPair keyPair, X500Name caName) throws Exception {
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
            caName,
            serialNumber,
            notBefore,
            notAfter,
            caName,
            SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded())
        );
        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
            .build(keyPair.getPrivate());
        return new JcaX509CertificateConverter().getCertificate(certBuilder.build(sigGen));
    }

    private X509Certificate createCertFile(
        X509Certificate ca, X500Name caName, X500Name certName, KeyPair caKey, KeyPair certPair, int validityDays
    ) throws Exception {
        PKCS10CertificationRequest csr = generateCSR(
            certName,
            certPair.getPublic(),
            certPair.getPrivate()
        );
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + validityDays * 24L * 60 * 60 * 1000);
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
            caName,
            serialNumber,
            notBefore,
            notAfter,
            certName,
            csr.getSubjectPublicKeyInfo()
        );
        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
            .build(caKey.getPrivate());
        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certBuilder.build(sigGen));
        certificate.checkValidity(new Date());
        certificate.verify(ca.getPublicKey());
        return certificate;
    }

    private PKCS10CertificationRequest generateCSR(
        X500Name subject, PublicKey publicKey, PrivateKey privateKey) throws Exception {
        JcaPKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(subject, publicKey);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(privateKey);
        return csrBuilder.build(signer);
    }

    private void printCert(X509Certificate cert, String title) throws Exception {
        System.out.println(title + ":");
        PemObject pemObject = new PemObject("CERTIFICATE", cert.getEncoded());
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.flush();
        System.out.println(stringWriter);
        System.out.println(Hex.encodeHexString(cert.getEncoded()));
    }
}
