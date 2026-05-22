import { test, expect } from "playwright/test";

const BASE_URL = "http://localhost:5173";

async function login(page: any) {
  await page.goto(`${BASE_URL}/login`);
  await page.waitForLoadState("networkidle");
  await page.locator("input[placeholder='请输入账号']").fill("admin");
  await page.locator("input[placeholder='请输入密码']").fill("admin123");
  await page.getByRole("button", { name: "登录 YouSells" }).click();
  await page.waitForURL("**/dashboard", { timeout: 10000 });
  await page.waitForLoadState("networkidle");
}

test.describe("FE-106 攻略区", () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
    await page.click("a[href='/topics']");
    await page.waitForURL("**/topics", { timeout: 10000 });
    await page.waitForLoadState("networkidle");
  });

  test("列表页标题和按钮渲染", async ({ page }) => {
    await expect(page.getByRole("heading", { name: "攻略区" })).toBeVisible();
    await expect(page.getByRole("button", { name: "+ 提问" })).toBeVisible();
  });

  test("筛选栏渲染", async ({ page }) => {
    await expect(page.locator(".topic-filter-bar")).toBeVisible();
    await expect(page.getByRole("radio", { name: "全部" })).toBeVisible();
    await expect(page.getByRole("radio", { name: "邀约" })).toBeVisible();
    await expect(page.getByRole("textbox", { name: "搜索问题标题" })).toBeVisible();
  });

  test("分类切换刷新列表", async ({ page }) => {
    await page.getByRole("radio", { name: "沟通" }).click();
    await page.waitForTimeout(300);
    await expect(page.locator(".topic-list, .el-empty")).toBeVisible();
  });

  test("提问弹窗表单校验", async ({ page }) => {
    await page.getByRole("button", { name: "+ 提问" }).click();
    await expect(page.getByRole("dialog", { name: "提问" })).toBeVisible();
    await page.getByRole("button", { name: "提交问题" }).click();
    await expect(page.getByText("请输入问题标题")).toBeVisible();
    await page.getByRole("button", { name: "取消" }).click();
  });

  test("空数据或列表渲染", async ({ page }) => {
    const hasCards = await page.locator(".topic-card").count() > 0;
    const hasEmpty = await page.locator(".el-empty").count() > 0;
    expect(hasCards || hasEmpty).toBe(true);
  });

  test("详情页渲染（如果有数据）", async ({ page }) => {
    const cardCount = await page.locator(".topic-card").count();
    if (cardCount === 0) {
      test.skip("暂无问题数据，跳过详情页测试");
      return;
    }
    await page.locator(".topic-card").first().click();
    await expect(page).toHaveURL(/\/topics\/\d+/);
    await expect(page.getByRole("button", { name: "返回列表" })).toBeVisible();
  });
});
