<#
Script para crear un Pipeline job en Jenkins usando la API REST.
- Requiere PowerShell 5+ (Windows) o PowerShell Core en Linux/Mac.
- Uso: rellenar las variables y ejecutar.

Advertencia: este script crea un job Pipeline (no Multibranch). Para Multibranch se necesita otra plantilla XML o usar Job DSL/Blue Ocean.
#>

param(
    [string]$JenkinsUrl = 'https://jenkins.example.com',
    [string]$Username = 'admin',
    [string]$ApiToken = '',
    [string]$JobName = 'backend-smash-order-pipeline',
    [string]$GitRepoUrl = 'https://github.com/JuanJoseUrbano/backend-restaurante-smash_order.git',
    [string]$Branch = 'develop',
    [string]$ScriptPath = 'Jenkinsfile'
)

if (-not $ApiToken) {
    Write-Error "ApiToken vacío. Crea un token en Jenkins (User -> Configure -> API Token) y vuelve a ejecutar."
    exit 1
}

$auth = "$Username`:$ApiToken"
$base64Auth = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($auth))
$headers = @{ Authorization = "Basic $base64Auth" }

# Plantilla XML para un Pipeline from SCM (Git)
$xml = @"
<flow-definition plugin="workflow-job@2.40">
  <description>Pipeline job creado por script</description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition" plugin="workflow-cps@2.92">
    <scm class="hudson.plugins.git.GitSCM" plugin="git@4.11.3">
      <configVersion>2</configVersion>
      <userRemoteConfigs>
        <hudson.plugins.git.UserRemoteConfig>
          <url>$GitRepoUrl</url>
        </hudson.plugins.git.UserRemoteConfig>
      </userRemoteConfigs>
      <branches>
        <hudson.plugins.git.BranchSpec>
          <name>$Branch</name>
        </hudson.plugins.git.BranchSpec>
      </branches>
      <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
      <submoduleCfg class="list"/>
      <extensions/>
    </scm>
    <scriptPath>$ScriptPath</scriptPath>
    <lightweight>true</lightweight>
  </definition>
  <triggers/>
  <disabled>false</disabled>
</flow-definition>
"@

$createUrl = "$JenkinsUrl/createItem?name=$JobName"

Write-Host "Creando job '$JobName' en $JenkinsUrl ..."
try {
    $resp = Invoke-RestMethod -Uri $createUrl -Method Post -Headers $headers -Body $xml -ContentType 'application/xml' -SkipCertificateCheck
    Write-Host "Job creado (o actualizado)."
} catch {
    Write-Error "Error al crear job: $_"
    exit 1
}

Write-Host "Hecho. Accede a: $JenkinsUrl/job/$JobName"
