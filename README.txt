WebServer is ran in an AWS EC2 instance with the following specifications:



EC2 instance:

	AMI: Amazon Linux 2 AMI (HVM) 64-bit(x86)
	Instance type: t2.micro
	subnet: us-east-1a
	Monitoring: Cloudwatch
	Storage: 8Gb
	Security group: 
		Inbound rules:
			80		TCP	0.0.0.0/0	CNV-ssh+http
			80		TCP	::/0	CNV-ssh+http
			8000	TCP	0.0.0.0/0	CNV-ssh+http
			22		TCP	0.0.0.0/0	CNV-ssh+http
		Outbound rules:
			All	All	0.0.0.0/0	CNV-ssh+http
			
Load Balancer:
	Classic load balancer for HTTP, HTTPS and TCP
	default VPC
	Load Balancer protocol: HTTP, port 80
	Instance protocol: HTTP, port 8000
	Security group: 
		Inbound rules:
			80		TCP	0.0.0.0/0
		Outbound rules:
			All	All	0.0.0.0/0
	Health check:
		protocol: HTTP
		port: 8000
		path: /test
		response timeout: 5s
		health check interval: 30s
		unhealthy threshold: 2
		healthy threshold: 10