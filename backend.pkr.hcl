packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = "~> 1"
    }
  }
}

source "amazon-ebs" "amz3_gp3" {
  ami_name      = "sivab-{{timestamp}}"
  instance_type = "t3.micro"
  region        = "us-east-1"

  source_ami_filter {
    filters = {
      name             = "al2023-ami-2023*"
      architecture     = "x86_64"
      root-device-type = "ebs"
    }
    most_recent = true
    owners      = ["amazon"]
  }

  ssh_username = "ec2-user"

  # Adding tags to the AMI
  tags = {
    Name        = "sivabj-packer-image"
    Environment = "Development"
    Owner       = "Konka"
    CreatedBy   = "Packer"
    Monitor     = "true"
  }
}

build {
  name    = "sivab"
  sources = ["source.amazon-ebs.amz3_gp3"]

  provisioner "file" {
    source      = "src"
    destination = "/app/src"
  }

  provisioner "file" {
    source      = "pom.xml"
    destination = "/app/pom.xml"
  }

  provisioner "shell" {
    inline = [
      "sudo mkdir -p /app",
      "sudo mv /app/src /app/",
      "sudo mv /app/pom.xml /app/",
      "sudo dnf install git ansible -y",
      "sudo git clone https://github.com/konka-devops-lab/ansible-roles.git /tmp/ansible-roles",
      "ansible-playbook /tmp/ansible-roles/ansible/backend.yml",
      "sudo rm -rf /tmp/ansible-roles",
      "sudo dnf remove git ansible -y"
    ]
  }
}