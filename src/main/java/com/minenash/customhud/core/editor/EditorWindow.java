package com.minenash.customhud.core.editor;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.minenash.customhud.core.registry.MetaData;
import com.minenash.customhud.core.registry.VariableRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class EditorWindow {


    //https://www.formdev.com/flatlaf/
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/customhud", "com.minenash.customhud.core.editor.SyntaxHighlighter");


        try {
            UIManager.setLookAndFeel( new FlatIntelliJLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        SwingUtilities.invokeLater(() -> {
            EditorWindow window = new EditorWindow();
        });
    }

    public EditorWindow() {
        JFrame frame = new JFrame();
        frame.setTitle("CustomHud Editor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(854, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setBackground(Color.darkGray);

//        frame.add(top(), BorderLayout.NORTH);
        frame.add(bottom(), BorderLayout.SOUTH);
        frame.add(center(), BorderLayout.CENTER);

        frame.setJMenuBar(MenuBar.build());
    }

    private JSplitPane center() {
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, insert(), editors());
        pane.setDividerLocation(300);
        return pane;
    }

    private int tabIndex = 3;
    private JPanel editors() {
        JPanel panel = new JPanel(){
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        panel.setLayout(new OverlayLayout(panel));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        tabbedPane.setOpaque(true);
        tabbedPane.setSize(600, 400);
        tabbedPane.add("Preview", preview());
        tabbedPane.add("Sections", new JPanel());
        tabbedPane.add("Editor", editor());
        tabbedPane.setSelectedIndex(2);

        JPanel toolBar = toolBar();

        tabbedPane.addChangeListener(e -> {
            tabIndex = tabbedPane.getSelectedIndex();
            panel.remove(toolBar);
            if (tabIndex == 2)
                panel.add(toolBar, 0);
        });

        panel.add(toolBar);
        panel.add(tabbedPane);

        return panel;
    }

    private JPanel editor() {
        JPanel panel = new JPanel(new BorderLayout());

        RSyntaxTextArea textArea = new RSyntaxTextArea();
//        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setSyntaxEditingStyle("text/customhud");
        textArea.setCodeFoldingEnabled(false);
        textArea.setSyntaxScheme(CToken.style);
        AutoComplete.createCompletionProvider(textArea);

        textArea.setText("""
                ==Section:TopLeft==
                {{fps >= 60, "&a", fps < 30, "&c", "&e"}}{fps}&f fps
                &cX&aY&bZ: &c{bx} &a{by} &b{bz} &f| &e{velocity_xz}
                &2Facing: &a{facing} &e({facing_towards_pn_sign}{facing_towards_xz})
                &2Biome: &a{biome} &7&o({dimension}) {{slime_chunk,"&bSlime"}}
                                
                &6Time: &e{hour24}:{minute}&6, Day: &e{day}
                &6D: &e{local_difficulty} &6// &e{clamped_local_difficulty}
                &6E: &e{entities_rendered} &6P: &e{particles}
                &6Light: {{light=0, "&c", "&e"}}{light}
                                
                &7This is an example profile
                &7Goto this link for details on how to edit
                &ehttps://customhud.dev/#docs
                                
                // Only download this mod from Modrinth or CurseForge
                // Don't download from 9minecraft, mediafire&c, or mega.nz
                                
                ==Section:TopRight==
                Mem: &e{memory_used_percentage}%&f of &e{memory_total}
                CPU Usage: &e{cpu}%
                Packets: &e{tx} {rx}
                {{tps, "TPS: &e{tps}", "Ping: &e{ping}"}}
                                
                {{target_block|target_fluid|target_entity, "&nTargets"}}
                {{target_block, "&e{target_block} &f| &c{tbx}, &a{tby}, &b{tbz}&f [Block]"}}
                {{target_fluid, "&e{target_fluid} &f| &c{tfx}, &a{tfy}, &b{tfz}&f [Fluid]"}}
                {{target_entity, "&e{target_entity} &f| &c{tex}, &a{tey}, &b{tez}&f [Entity]"}}
                {{target_block|target_fluid|target_entity, "&f"}}
                {{item:main|item:off, "&nItems in Hands"}}
                {{item:main, "&e{item:main} {{item:main:max_dur,'(&a{item:main:dur}&e/&a{item:main:max_dur}&e) '}}&f[Main]"}}
                {{item:off, "&e{item:off} {{item:off:max_dur,'(&a{item:off:dur}&e/&a{item:off:max_dur}&e) '}}&f[Off]"}}
                """);

        RTextScrollPane sp = new RTextScrollPane(textArea);
        panel.add(sp, BorderLayout.CENTER);



//        panel.add(toolBar, BorderLayout.NORTH);

        return panel;
    }

    private JPanel preview() {
        JPanel panel = new JPanel();
        ImageIcon icon = new ImageIcon("assets/custom_hud/icon.png");
        JLabel label = new JLabel(icon);
        label.setBorder(BorderFactory.createEmptyBorder(8,4,8,12));
        panel.add(label);

        AtomicReference<NativeImage> image = new AtomicReference<>();
        Timer t = new Timer(100, e -> {
            if (tabIndex == 0 && MinecraftClient.getInstance().getOverlay() == null)
                RenderSystem.recordRenderCall(() -> {
                    image.set(ScreenshotRecorder.takeScreenshot(MinecraftClient.getInstance().getFramebuffer()));
                });

            if (tabIndex == 0 && image.get() != null)
                try {
                    boolean useHeight = image.get().getWidth()/image.get().getHeight() < panel.getWidth()/panel.getHeight();
                    int width = useHeight ? -1 : panel.getWidth() - 16;
                    int height = useHeight ? panel.getHeight()-16 : -1;
                    icon.setImage(Toolkit.getDefaultToolkit().createImage(image.get().getBytes())
                            .getScaledInstance(width, height, 2));
                    SwingUtilities.updateComponentTreeUI(label);
                } catch (Exception ignored) {}

        });
        t.start();

        return panel;


    }

    private JPanel toolBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);
        toolBar.setMargin(new Insets(-4,0,0,0));

        toolBar.add( toolbarButton("Bold", "bold") );
        toolBar.add( toolbarButton("Italic", "italic") );
        toolBar.add( toolbarButton("Underline", "underline") );
        toolBar.add( toolbarButton("Strikethrough", "strikethrough") );
        toolBar.add( toolbarButton("Reset", "reset") );
//        toolBar.add( new JButton("Obvs") );
        toolBar.add(new JToolBar.Separator());
        toolBar.add( toolbarButtonS("Foreground Color", "foreground_color") );
        toolBar.add( toolbarButtonS("Background Color", "background_color") );

        panel.add(toolBar);

        return panel;
    }

    private JButton toolbarButton(String name, String img) {
        JButton button = new JButton(Utils.imageIcon("/assets/custom_hud/editor/" + img + ".png", name));
        button.setMargin(new Insets(1,1,1,1));
        return button;
    }
    private JButton toolbarButtonS(String name, String img) {
        JButton button = new JButton(Utils.imageIconS("/assets/custom_hud/editor/" + img + ".png", name));
        button.setMargin(new Insets(1,1,1,1));
        return button;
    }

    private JTabbedPane insert() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultMutableTreeNode variables = new DefaultMutableTreeNode("Variables");
        Map<String, DefaultMutableTreeNode> cats = new LinkedHashMap<>();

        for (MetaData var : VariableRegistry.metadata.values().stream().distinct().toList()) {
            cats.putIfAbsent(var.cat(), new DefaultMutableTreeNode(var.cat()));
            cats.get(var.cat()).add(new DefaultMutableTreeNode(var.name()));
        }

        for (DefaultMutableTreeNode cat : cats.values())
            variables.add( cat );

        JTree tree = new JTree(variables);
        tree.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
                        BorderFactory.createBevelBorder(BevelBorder.LOWERED)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.add(new JScrollPane( new JTree(variables) ), BorderLayout.CENTER);


        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
//
        JPanel config = new JPanel();
        config.setLayout(new BoxLayout(config, BoxLayout.Y_AXIS));
        config.add(new JLabel("Options for more advanced variables go here"));
        config.setBorder(BorderFactory.createTitledBorder("Configure"));
        panel2.add(config);

        JPanel flags = new JPanel();
        flags.setLayout(new BoxLayout(flags, BoxLayout.Y_AXIS));
        flags.add(new JLabel("Flags for the selected variable go here"));
        flags.setBorder(BorderFactory.createTitledBorder("Variable Flags"));
        panel2.add(flags);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Add", panel);
        tabbedPane.add("Edit", panel2);

        return tabbedPane;
    }

    private JPanel bottom() {
        JPanel left = new JPanel();
        JPanel right = new JPanel();

        JButton update = new JButton("Update");
        right.add(update);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

}
