#!/usr/bin/env bash
set -euo pipefail

# ==============================================
# YouSells 一键部署脚本
# 用法：./deploy.sh
# 前提：服务器已安装 Docker + Docker Compose
# ==============================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "=== 1/4 构建后端 JAR ==="
cd "$PROJECT_DIR/yousells-server"
JAVA_HOME="${JAVA_HOME:-}" mvn clean package -DskipTests -q
echo "  -> JAR 构建完成"

echo "=== 2/4 构建前端 dist ==="
cd "$PROJECT_DIR/yousells-web"
npm run build --silent 2>/dev/null || npm run build
echo "  -> dist 构建完成"

echo "=== 3/4 停止旧容器 ==="
cd "$SCRIPT_DIR"
docker compose down 2>/dev/null || true

echo "=== 4/4 启动服务 ==="
docker compose up -d --build

echo ""
echo "=== 等待服务健康检查 ==="
sleep 8
docker compose ps

echo ""
echo "=== 验证 ==="
curl -s http://localhost/actuator/health || echo "健康检查失败，请稍后重试"
echo ""
curl -s -o /dev/null -w "前端: HTTP %{http_code}\n" http://localhost/ || echo "前端检查失败"

echo ""
echo "部署完成！访问 http://<服务器IP> 即可。"
