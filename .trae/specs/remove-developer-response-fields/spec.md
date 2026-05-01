# 删除售后表冗余字段 Spec

## Why
`requirement_after_sale` 表中的 `developer_response` 和 `developer_response_time` 字段与 `after_sale_message` 表功能重复。前端已完全使用消息机制（`sendMessage`）进行对话，`respond` 接口从未被调用。删除这两个字段可以简化数据结构，避免数据不一致问题。

## What Changes
- **BREAKING** 删除 `requirement_after_sale` 表的 `developer_response` 和 `developer_response_time` 字段
- 删除后端 `/requirement/after-sale/respond/{id}` 接口
- 删除 `AfterSaleRespondDTO` 类
- 清理前端未使用的 `respond` 方法和接口定义中的冗余字段

## Impact
- Affected specs: 售后管理模块
- Affected code:
  - 后端: `RequirementAfterSale.java`, `RequirementAfterSaleVO.java`, `AfterSaleRespondDTO.java`, `RequirementAfterSaleServiceImpl.java`, `RequirementAfterSaleController.java`
  - 前端: `afterSale.ts`
  - 数据库: `api_platform.sql`, `api_platform_part1_user_requirement.sql`

## ADDED Requirements
无新增需求

## MODIFIED Requirements
### Requirement: 售后对话机制
售后对话统一通过 `after_sale_message` 表进行，支持申请人、开发者、管理员三方多轮对话。

## REMOVED Requirements
### Requirement: 开发者单次回应
**Reason**: 与 `after_sale_message` 表功能重复，且只能记录一次回应，无法支持多轮沟通
**Migration**: 所有对话统一使用 `after_sale_message` 表，通过 `sendMessage` 接口发送消息
