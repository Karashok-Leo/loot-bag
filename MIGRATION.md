# Loot Bag：NeoForge 1.21.1 迁移差异

基准：master `4c968beb39ad9be32305f2662d768f8cef1064fb`（Fabric 1.20.1）。目标分支：`neoforge-1.21.1`。模组版本仍为 1.3.1。

## 保持不变

- 三种袋子 Single / Optional / Random，四种奖励 Item / LootTable / Command / Effect，两种图标 Item / Texture。
- 随机权重算法、选项索引、奖励逻辑、潜行主手快速开启、潜行副手整叠开启、创造模式不消耗，以及预览界面流程。
- 所有既有资源、物品和自定义注册表 ID 仍使用 `loot-bag`；物品仍为 `loot-bag:loot_bag`。数据路径仍为 `data/<namespace>/loot-bag/{content,bag}`。
- 原始纹理、模型、语言文件和 MIT LICENSE 保持字节一致。
- 服务器同步仍为“内容 → 客户端数量 ACK → 袋子”，先建立内容引用再解码袋子。

## 必要的平台/API 变化

1. Fabric Loom / Fabric API 改为 NeoForge ModDev 2.0.148、NeoForge 21.1.252、Java 21、Gradle 9.2.0。采用 NeoForge Mojmap 命名，Minecraft 类型、方法及对应公共 API 参数出现映射名称变化，例如 Identifier→ResourceLocation、ServerPlayerEntity→ServerPlayer；项目自身公共类保留。
2. NeoForge 加载器 modId 为 `loot_bag`。新增 LOADER_ID，原 MOD_ID 的值 `loot-bag` 保持不变，避免更改数据和物品 ID。Fabric 元数据/入口改为 NeoForge 元数据、事件注册及客户端初始化。
3. 自定义注册表使用 RegistryBuilder/NewRegistryEvent/RegisterEvent，继续同步并保留 `Registry<T>` 公共字段。扩展模组需在 NeoForge 注册阶段调用相应 API。
4. 1.21 类型分派要求 MapCodec：BagType / ContentType / IconType 以及具体类型 CODEC 改为 MapCodec；Bag / Content / Icon 的统一分派入口仍是 Codec。
5. LootBagEntry 的旧 JSON Serializer 改为原生 MapCodec，并使用 List 条件/函数参数；`bag` 字段、type ID、权重、质量和 builder 功能保留。
6. BagId 使用 1.21 的 `minecraft:custom_data` 组件，键名仍为 BagId。命令示例：`/give @s loot-bag:loot_bag[minecraft:custom_data={BagId:"loot-bag:optional"}]`。
7. 按用户最新要求，物品对象直接使用原版 1.21 ItemStack.CODEC，保留原有物品 ID 字符串简写。没有 Count/tag 自动转换、DFU、额外兼容编解码器或旧格式判断分支。对象格式为 id/count/components，数量范围等规则由原版 Codec 决定；旧格式不提供迁移支持，也不额外改变原版对未知字段或默认值的处理。
8. 示例 diamond_sword 数据改为原生 count/components，仍分别表达实际奖励 Damage 66 和图标抢夺 III。示例 pack.mcmeta 改为支持数据包格式 48 / 资源包格式 34。
9. 新增仅影响 LootBagItem 的 ItemStack.getRarity Mixin，保留任意 BagId 栈、数据重载后的动态稀有度及附魔升阶。
10. Effect 类型改为 Holder<MobEffect>；动态附魔、组件、网络和数据生成使用 registry-aware serialization context。
11. 网络改为 CustomPacketPayload/StreamCodec 与 NeoForge 注册/发送 API，保留频道 ID、字段和同步顺序。
12. 渲染更换 VertexConsumer/BufferBuilder/Shader API；RGB 染色补不透明 alpha；显式恢复图标混合状态。保留原有渐变背景，避免新 Screen 自动添加模糊/重复背景。箭头按钮通过 StateSwitchingButton 适配器保留原纹理 UV 和悬停状态。
13. 数据生成 API 改为 PackOutput + CompletableFuture<HolderLookup.Provider> 与 GatherDataEvent，输出原生 1.21 示例；增加 pack.mcmeta 生成器，避免生成时丢失包元数据。

## 验证结果（2026-10-07）

- 完整 `build`、JAR 与 sources JAR：通过。
- 15 项 JUnit：通过。测试实际初始化 NeoForge 模组和 Mixin，覆盖原生物品组件、原版数量/默认值行为、动态稀有度、袋子算法、网络及战利品项 Codec。
- 隔离数据生成：通过，输出语言、7 个内容、3 个袋子和 pack.mcmeta。原纹理、模型、语言和 LICENSE 保持不变。
- 实际客户端：Minecraft 1.21.1 / NeoForge 21.1.252，加载最终打包 JAR，关闭源码模组发现。验证使用独立世界和测试数据包，测试数据未打包进模组。
- 默认空数据启动通过；无效袋子显示提示，5 个袋子保持不变。
- Single 预览、Escape 取消和生存开启通过：取消后袋子5/牛肉0；开启后袋子4/牛肉1。
- 主手潜行快速开启通过：袋子4/牛肉1 → 袋子3/牛肉2。副手整叠开启通过：袋子5 → 袋子0/牛肉5。
- Optional 潜行仍进入选择界面；前后循环、动画、选择钻石和 Absorption II 效果通过。可选袋子的提示仅包含预览说明。
- Command 奖励得到5个绿宝石，消耗1袋；LootTable 奖励得到4个金锭，消耗1袋。
- 创造模式开启后袋子仍为5，奖励牛肉1；原始 Single 示例的0.5倍图标和多行文本正常。
- 原生 Optional 示例的附魔剑图标正常；实际奖励耐久1495/1561，准确对应 Damage 66，未错误附加图标上的附魔。
- 原始 Random 示例自动轮播裁切腐肉和火药纹理；按库存键退出正常。确定性随机测试开启后袋子4/牛肉1。未刻意抽取原示例中的自杀命令，命令路径使用无害的确定性奖励验证，权重算法另有单测。
- 自定义战利品项生成1个可用袋子，开启后袋子0/牛肉1。
- `/reload` 将已有袋子稀有度从 RARE 更新为 EPIC，紫色名称与快速开启说明均正确；原栈无需替换，仍能预览和发放奖励。
- 保存退出后重新进入：袋子4/牛肉1保留，BagId和内容同步正常；再次开启得到袋子3/牛肉2。最后正常保存并退出客户端，退出码0。
- 一次界面输入异常的尝试未计入结果；重新确认焦点、重置测试输入后，剩余项目已逐项完成。

最终测试 JAR 的 SHA-256：
`a857c908146c65b0ce8be0a34e6ee8ab67037fa9b80122409abfa55cc5425c1b`

验证环境为 NeoForge ModDev 实际客户端加载打包 JAR；未单独验证官方安装器部署、专用服务器、独立多人联机或音频。环境日志存在离线认证/音频初始化警告，不影响上述已验证的功能。仓库基准没有 CI 工作流，本次未新增工作流、标签或 Release。

## 边界

未顺带加入参数安全校验、空选项 UI、玩法扩展或无关错误修复。第三方扩展需针对本分支重新编译；不承诺 Fabric 二进制兼容、跨版本网络互通或旧物品数据自动迁移。
