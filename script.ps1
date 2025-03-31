# Navigate to the project root directory
Set-Location "C:\Users\lp200\Desktop\CompletedDesign\code\BlockchainDonationSystem-Backend"

# Define all sub-services
$services = @("bds-gateway-service", "bds-voucher-service", "bds-user-service", "bds-project-service", "bds-donation-service", "bds-blockchain-service")

# Remote Docker registry address
$registry = "113.45.133.84:5000"

# Iterate over all services to build and push
foreach ($service in $services)
{
    Write-Host "Building $service ..."

    # Navigate to the subdirectory
    Set-Location $service

    # Execute Maven build
    mvn clean package -DskipTests

    # Build the Docker image
    docker build -t "$service`:latest" .

    # Tag the image
    docker tag "$service`:latest" "$registry/$service`:latest"

    # Push the image to the private registry
    docker push "$registry/$service`:latest"

    # Return to the parent directory
    Set-Location ..
}

Write-Host "All service Docker images have been successfully pushed to $registry 🎉"
