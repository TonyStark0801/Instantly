
# Instantly-App

![Instantly-Web Logo](https://github.com/TonyStark0801/Instantly-Web/blob/main/img/navLogo.jpg)

Introducing Instantly, the revolutionary peer-to-peer file transfer application that caters to your every need. Now, you can share files with other peers, regardless of their device, with just a click of a button. Whether you're on a smartphone or computer, Instantly has you covered with its innovative web app that allows for easy file transfers over the internet. Simply connect to the web and share files with lightning-fast speed and utmost security. Say goodbye to the traditional and tedious methods of file transfer and embrace Instantly – the game-changer in file sharing


## Introduction
Instantly-Web is a freeware, serverless, peer-to-peer file transfer web application that enables users to transfer files directly between devices without the need for a centralized server. This project is built using HTML5, CSS, Bootstrap, and Vanilla Javascript, and it leverages WebRTC technology through the PeerJS module and Firebase as a signaling server to establish a peer-to-peer connection and transfer data.



## Getting Started
You can access the web application on your browser through this URL: https://instantly-web.vercel.app. 

To establish a connection, the user can either copy and share a room link or get a QR code, which can be scanned through any scanner or the Instantly Android app. Once the link is entered into the browser, the connection is established, and the user can select or drop a file in the dropzone and click on the send button to begin the transfer.

## Features

-    Freeware, serverless, and peer-to-peer
-    Supports file transfer directly between devices
-    Uses WebRTC technology and Firebase as a signaling server
-    Supports file transfer of any type
-    Displays a percentage bar of data transferred
-    Downloads the file directly into the user's device upon completion of the transfer


## Limitations
Currently, the project only supports **one file transfer at a time**. In case the user wants to transfer multiple files at once, they need to convert those files to zip and then send it. Similarly, on the receiving end, they need to decompress the zip. The speed of transfer is limited by available network bandwidth and device hardware capabilities, and there is no specific limitation of speed by the site.
## How It Works
The Instantly-Web project uses WebRTC technology to establish a peer-to-peer connection between two users for file transfer. WebRTC is a free and open-source project that provides real-time communication capabilities to web browsers and mobile applications through simple APIs. WebRTC is supported by all major web browsers, including Chrome, Firefox, Safari, and Edge.

To establish a peer-to-peer connection, the Instantly-Web project uses the PeerJS module, which is built on top of WebRTC and provides a simple and consistent API for developers to build WebRTC applications. The PeerJS module simplifies the process of signaling and establishing a peer-to-peer connection between two clients by providing a signaling server that handles the initial handshake between clients.

The signaling server used in the Instantly-Web project is Firebase, a cloud-based platform that provides several services, including real-time databases, authentication, and hosting. The Firebase Realtime Database is used to store and exchange signaling data between clients, which includes session descriptions, ICE candidates, and other metadata required for establishing a peer-to-peer connection.

When a user wants to establish a connection, they can either copy and share a room link or get a QR code, which can be scanned through any scanner or the Instantly Android app. The link once entered into the browser will result in a connection.

Once the connection is established, the user can select or drop a file in the dropzone, and by clicking on the send button, the transfer begins. The data is divided into small chunks of 64KB and then transferred over the established peer-to-peer connection.

A percentage bar displays the total percent of data transferred till now, and once the entire blob of the file is received, it is directly downloaded into the user's device.

### Security
Security is a top priority in the Instantly-Web project.

- **WebRTC** uses end-to-end encryption to ensure that all data transferred between clients is secure and cannot be intercepted or modified by third parties. The encryption is achieved through the use of several security mechanisms:

    - **Datagram Transport Layer Security (DTLS)**: WebRTC uses DTLS to establish a secure communication channel between clients. DTLS is a protocol that provides secure communication over an unreliable network, such as the internet. It is similar to the Transport Layer Security (TLS) protocol used in HTTPS connections.

    - **Secure Real-time Transport Protocol (SRTP)**: WebRTC uses SRTP to provide secure transport of media data, such as audio and video. SRTP is an extension of the Real-time Transport Protocol (RTP) that provides confidentiality, integrity, and authentication of data.

    - **Network Address Translation (NAT) Traversal**: WebRTC uses several NAT traversal techniques to bypass firewalls and other network restrictions. These techniques include STUN, TURN, and ICE.

    - **Peer-to-Peer Encryption**: WebRTC uses peer-to-peer encryption to ensure that all data transferred between clients is encrypted end-to-end. This means that only the clients involved in the communication have access to the data.

- The **Firebase Realtime Database** also uses **SSL encryption** to secure all data transmitted between clients and the database server.

- **Vercel** provides several security features to protect websites and web applications hosted on their platform. Some of these security features include:
    - **SSL/TLS Encryption**: Vercel provides automatic SSL/TLS encryption for all custom domains hosted on their platform. This ensures that all data transferred between the client and the server is encrypted and secure.

    - **Distributed Denial of Service (DDoS) Protection**: Vercel provides built-in DDoS protection to protect websites from malicious traffic and attacks. Their network infrastructure is designed to detect and mitigate DDoS attacks in real-time.

    - **Firewall**: Vercel provides a built-in firewall that blocks malicious traffic and attacks, such as SQL injection and cross-site scripting (XSS) attacks.

    - **Continuous Security Scanning**: Vercel provides continuous security scanning to detect and prevent security vulnerabilities in websites and web applications hosted on their platform, etc.

In summary, the Instantly-Web project uses WebRTC and PeerJS to establish a peer-to-peer connection between two users for file transfer, and Firebase as a signaling server to handle the initial handshake between clients. The project also includes security features to ensure that all data transferred between clients is secure and private.
## Instantly - App
The Instantly application is also available as an Android app, providing a convenient way to transfer files locally between Android devices. By downloading and installing the Instantly Android app, users can easily establish a peer-to-peer connection and transfer files securely and quickly without the need for an internet connection. This feature offers an efficient and reliable option for local file sharing, making the Instantly Android app a valuable addition to the Instantly suite of products. Click below to access the Instantly Android app.

🔗 [Instantly-App Repo](https://github.com/TonyStark0801/instantly)

🔗 [Download Apk](https://github.com/TonyStark0801/Instantly/releases/download/Stable/Instantly.apk)


## Acknowledgements

I would like to thank the following resources for their invaluable support during the development of Instantly-Web:
- Documentations
    - Peerjs - [Peerjs API Documentation](https://peerjs.com/docs/#api)
    - Firebase - [Firebase Build Documentation](https://firebase.google.com/docs/build)
    - Bootstrap - [Bootstrap Documentation](https://getbootstrap.com/docs/5.3/getting-started/)
- Template - [Start Bootstrap New Age Template](https://startbootstrap.com/theme/new-age)
- GitHub - [GigaDrop Repository](https://github.com/VikashAnandJha/GigaDrop)

Thank you for providing these exceptional resources to the development community. Your contributions have been instrumental in the success of our project.


## Contributing

If you would like to contribute to this project, feel free to fork the repository and create a pull request. All contributions are always welcome!



## Conclusion
Thank you for your interest in Instantly-Web! If you have any feedback or questions about the project, please feel free to reach out to me on GitHub https://github.com/TonyStark0801
