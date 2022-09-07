#Build Instructions

./mvnw -DskipTests package

commit=$(git log -1 --pretty=%h) region=us-west-2 account=140199734014 repourl=${account}.dkr.ecr.${region}.amazonaws.com repo=cicd-images service=cloudez-reporting-api

aws ecr get-login-password --region {repourl} docker build -t ${service} . docker tag {account}.dkr.ecr.us-west-2.amazonaws.com/${repo}:${service}-master-${commit} docker push {region}.amazonaws.com/${repo}:${service}-master-${commit} docker rmi {repo}:${service}-master-${commit}

docker rmi $(docker images --filter "dangling=true" -q --no-trunc)
